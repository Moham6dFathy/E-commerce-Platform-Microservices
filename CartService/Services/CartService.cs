using CartService.Models;
using CartService.Models.DTOs;
using StackExchange.Redis;
using System.Text.Json;

namespace CartService.Services
{
    public class CartService : ICartService
    {
        private readonly IDatabase _database;
        private readonly ILogger<CartService> _logger;

        // TTL constants
        private static readonly TimeSpan DefaultTtl = TimeSpan.FromHours(24);
        private static readonly TimeSpan OrderedTtl = TimeSpan.FromDays(7);
        private static readonly TimeSpan PaymentConfirmedTtl = TimeSpan.FromDays(365); 

        public CartService(IConnectionMultiplexer redis, ILogger<CartService> logger)
        {
            _database = redis.GetDatabase();
            _logger = logger;
        }

        public async Task<CartResponse?> GetCart(int userId)
        {
            try
            {
                var cartKey = GetCartKey(userId);
                var cartJson = await _database.StringGetAsync(cartKey);
                
                if (!cartJson.HasValue)
                {
                    return null;
                }

                var cart = JsonSerializer.Deserialize<Cart>(cartJson!);
                return MapToCartResponse(cart!);
            }
            catch (Exception ex)
            {
                _logger.LogError(ex, "Error getting cart for user {UserId}", userId);
                throw;
            }
        }

        public async Task<CartResponse> AddToCart(int userId, AddToCartRequest request)
        {
            try
            {
                var cartKey = GetCartKey(userId);
                var cartJson = await _database.StringGetAsync(cartKey);
                
                Cart cart;
                if (cartJson.HasValue)
                {
                    cart = JsonSerializer.Deserialize<Cart>(cartJson!)!;
                }
                else
                {
                    cart = new Cart { UserId = userId };
                }

                // Update or add item
                var existingItem = cart.Items.FirstOrDefault(x => x.ProductId == request.ProductId);
                if (existingItem != null)
                {
                    existingItem.Quantity += request.Quantity;
                    existingItem.ExpiryTime = request.ExpiryTime ?? DateTime.UtcNow.AddHours(24);
                }
                else
                {
                    cart.Items.Add(new CartItem
                    {
                        CartItemId = Guid.NewGuid().ToString(),
                        UserId = userId,
                        ProductId = request.ProductId,
                        Quantity = request.Quantity,
                        ExpiryTime = request.ExpiryTime ?? DateTime.UtcNow.AddHours(24)
                    });
                }

                cart.UpdatedAt = DateTime.UtcNow;
                await SaveCartAsync(cart);
                
                return MapToCartResponse(cart);
            }
            catch (Exception ex)
            {
                _logger.LogError(ex, "Error adding item to cart for user {UserId}", userId);
                throw;
            }
        }

        public async Task<CartResponse> UpdateCartItem(int userId, int productId, UpdateCartItemRequest request)
        {
            try
            {
                var cartKey = GetCartKey(userId);
                var cartJson = await _database.StringGetAsync(cartKey);
                
                if (!cartJson.HasValue)
                {
                    throw new InvalidOperationException("Cart not found");
                }

                var cart = JsonSerializer.Deserialize<Cart>(cartJson!)!;
                var item = cart.Items.FirstOrDefault(x => x.ProductId == productId);
                
                if (item == null)
                {
                    throw new InvalidOperationException("Item not found in cart");
                }

                item.Quantity = request.Quantity;
                cart.UpdatedAt = DateTime.UtcNow;
                await SaveCartAsync(cart);
                
                return MapToCartResponse(cart);
            }
            catch (Exception ex)
            {
                _logger.LogError(ex, "Error updating cart item for user {UserId}, product {ProductId}", userId, productId);
                throw;
            }
        }

        public async Task<bool> RemoveFromCart(int userId, int productId)
        {
            try
            {
                var cartKey = GetCartKey(userId);
                var cartJson = await _database.StringGetAsync(cartKey);
                
                if (!cartJson.HasValue)
                {
                    return false;
                }

                var cart = JsonSerializer.Deserialize<Cart>(cartJson!)!;
                var item = cart.Items.FirstOrDefault(x => x.ProductId == productId);
                
                if (item == null)
                {
                    return false;
                }

                cart.Items.Remove(item);
                cart.UpdatedAt = DateTime.UtcNow;
                await SaveCartAsync(cart);
                
                return true;
            }
            catch (Exception ex)
            {
                _logger.LogError(ex, "Error removing item from cart for user {UserId}, product {ProductId}", userId, productId);
                throw;
            }
        }

        public async Task<bool> ClearCart(int userId)
        {
            try
            {
                var cartKey = GetCartKey(userId);
                return await _database.KeyDeleteAsync(cartKey);
            }
            catch (Exception ex)
            {
                _logger.LogError(ex, "Error clearing cart for user {UserId}", userId);
                throw;
            }
        }


        private async Task SaveCartAsync(Cart cart)
        {
            var ttl = GetTtlForStatus(cart.Status);
            await SaveCartWithTtlAsync(cart, ttl);
        }

        private async Task SaveCartWithTtlAsync(Cart cart, TimeSpan ttl)
        {
            var cartKey = GetCartKey(cart.UserId);
            var cartJson = JsonSerializer.Serialize(cart);
            await _database.StringSetAsync(cartKey, cartJson, ttl);
        }

        private TimeSpan GetTtlForStatus(CartStatus status)
        {
            return status switch
            {
                CartStatus.Active => DefaultTtl,
                CartStatus.Ordered => OrderedTtl,
                CartStatus.PaymentConfirmed => PaymentConfirmedTtl,
                _ => DefaultTtl
            };
        }

        private string GetCartKey(int userId)
        {
            return $"cart:{userId}";
        }

        private CartResponse MapToCartResponse(Cart cart)
        {
            return new CartResponse
            {
                UserId = cart.UserId,
                Items = cart.Items.Select(item => new CartItemResponse
                {
                    CartItemId = item.CartItemId,
                    UserId = item.UserId,
                    ProductId = item.ProductId,
                    Quantity = item.Quantity,
                    ExpiryTime = item.ExpiryTime
                }).ToList(),
                TotalItems = cart.TotalItems,
            };
        }
    }
}
