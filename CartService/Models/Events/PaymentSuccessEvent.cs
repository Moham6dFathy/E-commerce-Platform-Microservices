public class PaymentSuccessEvent
{
    public int PaymentId { get; set; }
    public int OrderId { get; set; }
    public int UserId { get; set; }
    public double Amount { get; set; }
    public string Status { get; set; } = string.Empty;
    public DateTime PaymentDate { get; set; }
}