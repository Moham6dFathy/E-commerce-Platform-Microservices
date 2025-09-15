using Microsoft.AspNetCore.Authorization;
using Microsoft.AspNetCore.Mvc;
using PaymentService.Models;
using PaymentService.Services;
using PaymentService.Models.DTOs;

namespace PaymentService.Controllers;


[Authorize]
[ApiController]
[Route("api/v1/[controller]")]
public class PaymentController : ControllerBase
{
    private readonly IPaymentService _paymentService;

    public PaymentController(IPaymentService paymentService)
    {
        _paymentService = paymentService;
    }

    [HttpPost]
    public async Task<ActionResult<PaymentResponse>> ProcessPayment([FromBody] PaymentRequest request)
    {
        try
        {
            var result = await _paymentService.ProcessPaymentAsync(request);
            return Ok(result);
        }
        catch (Exception ex)
        {
            return StatusCode(500, $"Internal server error: {ex.Message}");
        }
    }

    [HttpGet("{id}")]
    public async Task<ActionResult<PaymentResponse>> GetPayment(int id)
    {
        try
        {
            var payment = await _paymentService.GetPaymentByIdAsync(id);
            return Ok(payment);
        }
        catch (KeyNotFoundException ex)
        {
            return NotFound(ex.Message);
        }
        catch (Exception ex)
        {
            return StatusCode(500, $"Internal server error: {ex.Message}");
        }
    }

    [HttpGet("user/{userId}")]
    public async Task<ActionResult<IEnumerable<PaymentResponse>>> GetPaymentsByUser(int userId)
    {
        try
        {
            var payments = await _paymentService.GetPaymentsByUserIdAsync(userId);
            return Ok(payments);
        }
        catch (Exception ex)
        {
            return StatusCode(500, $"Internal server error: {ex.Message}");
        }
    }
}
