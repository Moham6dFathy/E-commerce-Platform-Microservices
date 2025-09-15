package org.example.orderservice.events;

import jakarta.validation.constraints.NotBlank;
import lombok.*;
import org.example.orderservice.model.OrderItem;

import java.io.Serializable;
import java.util.List;
import java.util.Map;

@Setter
@Getter
@AllArgsConstructor
@NoArgsConstructor
public class OrderCreatedEvent implements Serializable {
    private String orderId;
    private String status;
    private List<OrderItem> items;

    @AllArgsConstructor
    public static class OrderItem {
        private int productId;
        private int quantity;


        public int getProductId() { return productId; }
        public void setProductId(int productId) { this.productId = productId; }

        public int getQuantity() { return quantity; }
        public void setQuantity(int quantity) { this.quantity = quantity; }
    }

}