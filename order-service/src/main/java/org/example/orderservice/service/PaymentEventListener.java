package org.example.orderservice.service;

import org.example.orderservice.dto.PaymentSuccessEvent;
import org.example.orderservice.model.Order;
import org.example.orderservice.repository.OrderRepository;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.stereotype.Service;

@Service
public class PaymentEventListener {

    private final OrderRepository orderRepository;

    public PaymentEventListener(OrderRepository orderRepository) {
        this.orderRepository = orderRepository;
    }

    @RabbitListener(queues = "order.payment.success")
    public void handlePaymentSuccess(PaymentSuccessEvent event) {
        System.out.println("✅ Received PaymentSuccessEvent: " + event.getOrderId());
        System.out.println(event);

        Order order = orderRepository.findById(event.getOrderId())
                .orElseThrow(() -> new RuntimeException("Order not found with id " + event.getOrderId()));

        if (!"PAID".equals(order.getStatus())) {
            order.setStatus("PAID");
            orderRepository.save(order);
            System.out.println("🔄 Order " + event.getOrderId() + " status updated to PAID");
        } else {
            System.out.println("⚠️ Order " + event.getOrderId() + " already PAID, skipping update.");
        }
    }
}
