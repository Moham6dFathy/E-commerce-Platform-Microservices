using CartService.Models;
using CartService.Models.DTOs;

namespace CartService.Services
{
    public interface ICartService
    {
        Task<CartResponse?> GetCart(int userId);
        Task<CartResponse> AddToCart(int userId, AddToCartRequest request);
        Task<CartResponse> UpdateCartItem(int userId, int productId, UpdateCartItemRequest request);
        Task<bool> RemoveFromCart(int userId, int productId);
        Task<bool> ClearCart(int userId);
    }
}
