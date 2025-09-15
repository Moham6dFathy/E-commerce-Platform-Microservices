using System.Text;
using System.Text.Json;
using CartService.Models;
using CartService.Models.DTOs;
using Microsoft.Extensions.DependencyInjection;
using Microsoft.Extensions.Hosting;
using Microsoft.Extensions.Logging;
using RabbitMQ.Client;
using RabbitMQ.Client.Events;
using StackExchange.Redis;

namespace CartService.Services
{
    public class RabbitMqListener : BackgroundService
    {
        private readonly IServiceProvider _serviceProvider;
        private readonly ILogger<RabbitMqListener> _logger;
        private IConnection _connection;
        private IModel _channel;

        public RabbitMqListener(IServiceProvider serviceProvider, ILogger<RabbitMqListener> logger)
        {
            _serviceProvider = serviceProvider;
            _logger = logger;

            var factory = new ConnectionFactory
            {
                HostName = "localhost", 
                Port = 5672,
                UserName = "myuser",
                Password = "mypassword",
            };
  
            _connection = factory.CreateConnection();
            _channel = _connection.CreateModel();

            _channel.ExchangeDeclare(exchange: "payments", type: ExchangeType.Topic, durable: true);
            _channel.QueueDeclare(queue: "cart.payment.success", durable: true, exclusive: false, autoDelete: false);
            _channel.QueueBind("cart.payment.success", "payments", "payment.success");
        }

        protected override Task ExecuteAsync(CancellationToken stoppingToken)
        {
            var consumer = new EventingBasicConsumer(_channel);
            consumer.Received += async (ch, ea) =>
            {
                try
                {
                    var body = ea.Body.ToArray();
                    var message = Encoding.UTF8.GetString(body);

                    var paymentEvent = JsonSerializer.Deserialize<PaymentSuccessEvent>(message);
                    if (paymentEvent == null)
                    {
                        _logger.LogWarning("Received null PaymentSuccessEvent");
                        _channel.BasicAck(ea.DeliveryTag, false);
                        return;
                    }

                    _logger.LogInformation("✅ CartService received PaymentSuccessEvent for UserId={UserId}, OrderId={OrderId}",
                        paymentEvent.UserId, paymentEvent.OrderId);

                    using var scope = _serviceProvider.CreateScope();
                    var redis = scope.ServiceProvider.GetRequiredService<IConnectionMultiplexer>().GetDatabase();

                    var cartKey = $"cart:{paymentEvent.UserId}";
                    var cartJson = await redis.StringGetAsync(cartKey);

                    if (cartJson.HasValue)
                    {
                        var cart = JsonSerializer.Deserialize<Cart>(cartJson!)!;
                        cart.Status = CartStatus.PaymentConfirmed;
                        cart.UpdatedAt = DateTime.UtcNow;

                        // save cart again
                        var updatedJson = JsonSerializer.Serialize(cart);
                        await redis.StringSetAsync(cartKey, updatedJson);

                        // 🚀 remove TTL so cart stays forever
                        await redis.KeyPersistAsync(cartKey);

                        _logger.LogInformation("🛒 Cart for user {UserId} marked as PaymentConfirmed and TTL removed.",
                            paymentEvent.UserId);
                    }
                    else
                    {
                        _logger.LogWarning("⚠️ Cart not found for user {UserId}", paymentEvent.UserId);
                    }

                    _channel.BasicAck(ea.DeliveryTag, false);
                }
                catch (Exception ex)
                {
                    _logger.LogError(ex, "❌ Error handling PaymentSuccessEvent in CartService");
                    // no ack → message will be retried
                }
            };

            _channel.BasicConsume("cart.payment.success", autoAck: false, consumer: consumer);

            return Task.CompletedTask;
        }

        public override void Dispose()
        {
            _channel?.Close();
            _connection?.Close();
            base.Dispose();
        }
    }
}
