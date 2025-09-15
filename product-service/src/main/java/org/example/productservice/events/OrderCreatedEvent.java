package org.example.productservice.events;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

import java.io.Serializable;
import java.util.List;

@Setter
@Getter
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
