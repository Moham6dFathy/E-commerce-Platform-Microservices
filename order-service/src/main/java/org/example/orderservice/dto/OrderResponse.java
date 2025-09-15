package org.example.orderservice.dto;

import org.example.orderservice.model.OrderItem;

import java.time.LocalDateTime;
import java.util.List;

public record OrderResponse(String message, int id, int userId, List<OrderItemsResponse> orderItems, LocalDateTime orderDate, String orderStatus, double totalAmount ) {
}
