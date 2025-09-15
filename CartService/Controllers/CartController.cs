using CartService.Models.DTOs;
using CartService.Services;
using Microsoft.AspNetCore.Authorization;
using Microsoft.AspNetCore.Mvc;
using System.Security.Claims;

namespace CartService.Controllers
{
    [ApiController]
    [Route("api/v1/[controller]")]
    [Authorize]
    public class CartController : ControllerBase
    {
        private readonly ICartService _cartService;
        private readonly ILogger<CartController> _logger;

        public CartController(ICartService cartService, ILogger<CartController> logger)
        {
            _cartService = cartService;
            _logger = logger;
        }

        [HttpGet]
        public async Task<ActionResult<CartResponse>> GetCart()
        {
            try
            {
                var userId = GetUserId();
                var cart = await _cartService.GetCart(userId);
                
                if (cart == null)
                {
                    return Ok(new CartResponse { UserId = userId });
                }

                return Ok(cart);
            }
            catch (Exception ex)
            {
                _logger.LogError(ex, "Error getting cart for user");
                return StatusCode(500, "Internal server error");
            }
        }

        [HttpPost("items")]
        public async Task<ActionResult<CartResponse>> AddToCart([FromBody] AddToCartRequest request)
        {
            try
            {
                if (!ModelState.IsValid)
                {
                    return BadRequest(ModelState);
                }

                var userId = GetUserId();
                var cart = await _cartService.AddToCart(userId, request);
                
                return Ok(cart);
            }
            catch (Exception ex)
            {
                _logger.LogError(ex, "Error adding item to cart");
                return StatusCode(500, "Internal server error");
            }
        }

        [HttpPut("items/{productId}")]
        public async Task<ActionResult<CartResponse>> UpdateCartItem(int productId, [FromBody] UpdateCartItemRequest request)
        {
            try
            {
                if (!ModelState.IsValid)
                {
                    return BadRequest(ModelState);
                }

                var userId = GetUserId();
                var cart = await _cartService.UpdateCartItem(userId, productId, request);
                
                return Ok(cart);
            }
            catch (InvalidOperationException ex)
            {
                _logger.LogWarning(ex, "Cart or item not found for user {UserId}, product {ProductId}", GetUserId(), productId);
                return NotFound(ex.Message);
            }
            catch (Exception ex)
            {
                _logger.LogError(ex, "Error updating cart item");
                return StatusCode(500, "Internal server error");
            }
        }

        [HttpDelete("items/{productId}")]
        public async Task<ActionResult> RemoveFromCart(int productId)
        {
            try
            {
                var userId = GetUserId();
                var removed = await _cartService.RemoveFromCart(userId, productId);
                
                if (!removed)
                {
                    return NotFound("Item not found in cart");
                }

                return NoContent();
            }
            catch (Exception ex)
            {
                _logger.LogError(ex, "Error removing item from cart");
                return StatusCode(500, "Internal server error");
            }
        }

        [HttpDelete]
        public async Task<ActionResult> ClearCart()
        {
            try
            {
                var userId = GetUserId();
                var cleared = await _cartService.ClearCart(userId);
                
                if (!cleared)
                {
                    return NotFound("Cart not found");
                }

                return NoContent();
            }
            catch (Exception ex)
            {
                _logger.LogError(ex, "Error clearing cart");
                return StatusCode(500, "Internal server error");
            }
        }

        private int GetUserId()
        {
            var userIdClaim = User.FindFirst("userId")?.Value;
                
            if (string.IsNullOrEmpty(userIdClaim) || !int.TryParse(userIdClaim, out int userId))
            {
                _logger.LogWarning("Token payload: {Claims}", string.Join(", ", User.Claims.Select(c => $"{c.Type}: {c.Value}")));
                throw new UnauthorizedAccessException("User ID not found in token or invalid format");
            }

            return userId;
        }
    }
}
