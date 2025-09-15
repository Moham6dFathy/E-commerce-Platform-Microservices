namespace PaymentService.Models.DTOs;

public class PaymentResponse
{
    public int Id { get; set; }
    public int OrderId { get; set; }
    public int UserId { get; set; }
    public string Status { get; set; } = string.Empty;
    public DateTime PaymentDate { get; set; }
    public double Amount { get; set; }
}
