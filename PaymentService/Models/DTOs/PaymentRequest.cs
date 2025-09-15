using System.ComponentModel.DataAnnotations;

namespace PaymentService.Models.DTOs;

public class PaymentRequest
{
    [Required]
    public int OrderId { get; set; }

    [Required]
    public int UserId { get; set; }
}
