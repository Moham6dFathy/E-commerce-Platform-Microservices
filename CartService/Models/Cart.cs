namespace CartService.Models
{
    public class Cart
    {
        public int UserId { get; set; }
        public List<CartItem> Items { get; set; } = new List<CartItem>();
        public int TotalItems => Items.Sum(item => item.Quantity);
        public DateTime CreatedAt { get; set; } = DateTime.UtcNow;
        public DateTime UpdatedAt { get; set; } = DateTime.UtcNow;
        public CartStatus Status { get; set; } = CartStatus.Active;
    }

    public enum CartStatus
    {
        Active,
        Ordered,
        PaymentConfirmed
    }
}
