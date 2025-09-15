package org.example.orderservice.service;

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.example.orderservice.client.AuthClient;
import org.example.orderservice.client.ProductClient;
import org.example.orderservice.dto.*;
import org.example.orderservice.events.OrderCreatedEvent;
import org.example.orderservice.model.Order;
import org.example.orderservice.model.OrderItem;
import org.example.orderservice.repository.OrderRepository;
import org.springframework.stereotype.Repository;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@Service
@Slf4j
@RequiredArgsConstructor
public class OrderService {
    private final OrderRepository orderRepository;
    private final ProductClient productClient;
    private final AuthClient authClient;
    private final OrderProducer orderProducer;

    @Transactional
    public OrderResponse placeOrder(Integer userId, List<OrderItemRequest> orderItemsRequests){
        UserResponse user = authClient.getUserById(String.valueOf(userId));

        if(user.id() != userId){
            throw new RuntimeException("userId is not matched with Loging user");
        }

        Order order = new Order();
        order.setUserId(userId);

        double totalAmount = 0;
        List<OrderCreatedEvent.OrderItem> orderItems = new ArrayList<>();
        for(OrderItemRequest orderItemRequest : orderItemsRequests){
            ProductResponse product = productClient.getProductById(orderItemRequest.productId());
            OrderItem orderItem = new OrderItem();
            orderItem.setProductId(orderItemRequest.productId());
            orderItem.setQuantity(orderItemRequest.quantity());
            orderItem.setPrice(product.price());

            orderItems.add(new OrderCreatedEvent.OrderItem(orderItem.getProductId(),orderItem.getQuantity()));

            order.addOrderItem(orderItem);
            totalAmount += orderItemRequest.quantity() * product.price();
        }
        order.setStatus("PENDING");
        order.setOrderDate(LocalDateTime.now());
        order.setTotalAmount(totalAmount);

        orderRepository.save(order);


        String orderId = String.valueOf(order.getId());

        // Publish Order Created (RabbitMQ)
        OrderCreatedEvent event = new OrderCreatedEvent(orderId, order.getStatus(),orderItems);
        orderProducer.publishOrderCreated(event);

        List<OrderItemsResponse> orderItemsResponses = order.getOrderItems()
                .stream()
                .map(orderItem -> new OrderItemsResponse(
                        orderItem.getProductId()
                        ,orderItem.getQuantity()
                        ,orderItem.getPrice()))
                .toList();

        return new OrderResponse("Order Created Successfully"
                ,order.getId()
                ,order.getUserId()
                ,orderItemsResponses
                ,order.getOrderDate()
                ,order.getStatus()
                ,totalAmount);

    }

    public List<OrderResponse> getOrders() {
        return orderRepository.findAll()
                .stream()
                .map(order->new OrderResponse("Order Details :"
                        ,order.getId()
                        ,order.getUserId()
                        ,order.getOrderItems()
                        .stream()
                        .map(orderItem -> new OrderItemsResponse(
                                orderItem.getProductId()
                                ,orderItem.getQuantity()
                                ,orderItem.getPrice()))
                        .toList()
                        ,order.getOrderDate()
                        ,order.getStatus()
                        ,order.getTotalAmount()))
                .toList();

    }

    public OrderResponse getOrderById(int orderId){
        Order order = orderRepository.findById(orderId)
                .orElseThrow(()-> new RuntimeException("Order Not Found"));

        List<OrderItemsResponse> orderItems = order.getOrderItems()
                .stream()
                .map(orderItem -> new OrderItemsResponse(
                        orderItem.getProductId()
                        ,orderItem.getQuantity()
                        ,orderItem.getPrice()))
                .toList();
        return new OrderResponse("Order Details :"
                ,order.getId()
                ,order.getUserId()
                ,orderItems
                ,order.getOrderDate()
                ,order.getStatus()
                ,order.getTotalAmount());
    }

    public List<OrderResponse> getOrdersByUser(int userId){
        return orderRepository.getOrdersByUserId(userId)
                .stream()
                .map(order -> new OrderResponse("Order Details :"
                        ,order.getId()
                        ,order.getUserId()
                        ,order.getOrderItems()
                        .stream()
                        .map(orderItem -> new OrderItemsResponse(
                                orderItem.getProductId()
                                ,orderItem.getQuantity()
                                ,orderItem.getPrice()))
                        .toList()
                        ,order.getOrderDate()
                        ,order.getStatus()
                        ,order.getTotalAmount()))
                .toList();
    }
}
