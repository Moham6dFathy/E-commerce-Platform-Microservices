using System.Text;
using System.Text.Json;
using RabbitMQ.Client;

namespace PaymentService.Services
{
    public interface IRabbitMqPublisher
    {
        void Publish(string routingKey, object message);
    }

    public class RabbitMqPublisher : IRabbitMqPublisher, IDisposable
    {
        private readonly IConnection _connection;
        private readonly IModel _channel;

        public RabbitMqPublisher()
        {
            var factory = new ConnectionFactory()
            {
                HostName = "localhost",
                Port = 5672,
                UserName = "myuser",
                Password = "mypassword"
            };

            _connection = factory.CreateConnection();
            _channel = _connection.CreateModel();

            _channel.ExchangeDeclare(exchange: "payments", type: ExchangeType.Topic, durable: true);
        }

        public void Publish(string routingKey, object message)
        {
            var body = Encoding.UTF8.GetBytes(JsonSerializer.Serialize(message));

            _channel.BasicPublish(
                exchange: "payments",
                routingKey: routingKey, 
                basicProperties: null,
                body: body
            );

            Console.WriteLine($"✅ Published event to '{routingKey}': {JsonSerializer.Serialize(message)}");
        }

        public void Dispose()
        {
            _channel?.Close();
            _connection?.Close();
        }
    }
}
