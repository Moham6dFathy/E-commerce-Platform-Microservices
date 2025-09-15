package org.example.orderservice.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;

@Getter
@Setter
public class PaymentSuccessEvent {
    @JsonProperty("PaymentId")
    private int paymentId;

    @JsonProperty("OrderId")
    private int orderId;

    @JsonProperty("UserId")
    private int userId;

    @JsonProperty("Amount")
    private double amount;

    @JsonProperty("Status")
    private String status;

    @JsonProperty("PaymentDate")
    private LocalDateTime paymentDate;
}
