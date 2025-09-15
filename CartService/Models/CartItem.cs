namespace CartService.Models
{
    public class CartItem
    {
        public string CartItemId { get; set; } = string.Empty;
        public int UserId { get; set; }
        public int ProductId { get; set; }
        public int Quantity { get; set; }
        public DateTime ExpiryTime { get; set; }
    }
}
