namespace CartService.Models.DTOs
{
    public class CartResponse
    {
        public int UserId { get; set; }
        public List<CartItemResponse> Items { get; set; } = new List<CartItemResponse>();
        public int TotalItems { get; set; }
    }

    public class CartItemResponse
    {
        public string CartItemId { get; set; } = string.Empty;
        public int UserId { get; set; }
        public int ProductId { get; set; }
        public int Quantity { get; set; }
        public DateTime ExpiryTime { get; set; }
    }
}
