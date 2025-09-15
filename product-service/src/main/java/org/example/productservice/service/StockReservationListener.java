package org.example.productservice.service;

import org.example.productservice.events.OrderCreatedEvent;
import org.example.productservice.model.Product;
import org.example.productservice.repository.ProductRepository;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class StockReservationListener {

    private final ProductRepository productRepository;

    public StockReservationListener(ProductRepository productRepository) {
        this.productRepository = productRepository;
    }

    @Transactional
    @RabbitListener(queues = "order.created.queue")
    public void handleOrderCreated(OrderCreatedEvent event) {
        System.out.println("📥 Received OrderCreatedEvent: " + event.getOrderId());

        for (OrderCreatedEvent.OrderItem item : event.getItems()) {
            Product product = productRepository.findById(item.getProductId())
                    .orElseThrow(() -> new RuntimeException("Product not found: " + item.getProductId()));

            if (product.getStock() >= item.getQuantity()) {
                product.setStock(product.getStock() - item.getQuantity());
                productRepository.save(product);
                System.out.println("✅ Reserved " + item.getQuantity() + " of Product " + product.getId());
            } else {
                System.out.println("❌ Not enough stock for Product " + product.getId());
            }
        }
    }
}

