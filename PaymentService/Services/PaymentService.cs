using PaymentService.Data;
using PaymentService.Models;
using Microsoft.EntityFrameworkCore;
using PaymentService.Models.DTOs;


namespace PaymentService.Services;

public class PaymentService : IPaymentService
{
    private readonly PaymentDbContext _dbContext;
    private readonly IOrderServiceClient _orderServiceClient;
    private readonly IRabbitMqPublisher _publisher;

    public PaymentService(
        PaymentDbContext dbContext,
        IOrderServiceClient orderServiceClient,
        IRabbitMqPublisher publisher)
    {
        _dbContext = dbContext;
        _orderServiceClient = orderServiceClient;
        _publisher = publisher;
    }

    public async Task<PaymentResponse> ProcessPaymentAsync(PaymentRequest request)
    {
        int orderId = request.OrderId;
        int userId = request.UserId;
 
        double amount = await _orderServiceClient.GetOrderAmountAsync(orderId);

        var payment = new Payment
        {
            UserId = userId,
            OrderId = orderId,
            Amount = amount,
            Status = "Completed",
            PaymentDate = DateTime.UtcNow
        };

        _dbContext.Payments.Add(payment);
        await _dbContext.SaveChangesAsync();

        // Publish to RabbitMQ
        var paymentSuccessEvent = new
        {
            PaymentId = payment.Id,
            OrderId = payment.OrderId,
            UserId = payment.UserId,
            Amount = payment.Amount,
            Status = payment.Status,
            PaymentDate = payment.PaymentDate
        };

        _publisher.Publish("payment.success", paymentSuccessEvent);

        return MapToResponse(payment);
    }


    public async Task<PaymentResponse> GetPaymentByIdAsync(int id)
    {
        var payment = await _dbContext.Payments.FindAsync(id)
            ?? throw new KeyNotFoundException($"Payment with ID {id} not found");

        return MapToResponse(payment);
    }

    public async Task<IEnumerable<PaymentResponse>> GetPaymentsByUserIdAsync(int userId)
    {
        var payments = await _dbContext.Payments
            .Where(p => p.UserId == userId)
            .OrderByDescending(p => p.PaymentDate)
            .ToListAsync();

        return payments.Select(MapToResponse);
    }

    private static PaymentResponse MapToResponse(Payment payment)
    {
        return new PaymentResponse
        {
            Id = payment.Id,
            OrderId = payment.OrderId,
            UserId = payment.UserId,
            Status = payment.Status,
            PaymentDate = payment.PaymentDate,
            Amount = payment.Amount
        };
    }

}