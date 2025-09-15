using PaymentService.Models.DTOs;

namespace PaymentService.Services;

public interface IPaymentService
{
    Task<PaymentResponse> ProcessPaymentAsync(PaymentRequest request);
    Task<PaymentResponse> GetPaymentByIdAsync(int id);
    Task<IEnumerable<PaymentResponse>> GetPaymentsByUserIdAsync(int userId);
}
