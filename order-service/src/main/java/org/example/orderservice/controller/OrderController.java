package org.example.orderservice.controller;

import lombok.RequiredArgsConstructor;
import org.example.orderservice.dto.OrderItemRequest;
import org.example.orderservice.dto.OrderResponse;
import org.example.orderservice.security.JwtUtil;
import org.example.orderservice.service.OrderService;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/order")
@RequiredArgsConstructor
public class OrderController {
    private final OrderService orderService;
    private final JwtUtil jwtUtil;

    @PreAuthorize("hasAuthority('CUSTOMER')")
    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public OrderResponse placeOrder( @RequestHeader("Authorization") String token , @RequestBody List<OrderItemRequest> orderItemRequests) {
        String jwt = token.replace("Bearer ", "");
        Integer userId = jwtUtil.getUserIdFromToken(jwt);
        return orderService.placeOrder(userId,orderItemRequests);
    }

    @PreAuthorize("hasAuthority('CUSTOMER')")
    @GetMapping("/me")
    @ResponseStatus(HttpStatus.OK)
    public List<OrderResponse> getOrdersOfCurrentUser(@RequestHeader("Authorization") String token){
        String jwt = token.replace("Bearer ", "");
        Integer userId = jwtUtil.getUserIdFromToken(jwt);
        return orderService.getOrdersByUser(userId);
    }

    @PreAuthorize("hasAuthority('ADMIN')")
    @GetMapping
    @ResponseStatus(HttpStatus.OK)
    public List<OrderResponse> getOrders() {
        return orderService.getOrders();
    }

    @GetMapping("/{id}")
    @ResponseStatus(HttpStatus.OK)
    public OrderResponse getOrderById(@PathVariable("id") int id) {
        return orderService.getOrderById(id);
    }

    @GetMapping("/{id}/amount")
    @ResponseStatus(HttpStatus.OK)
    public Double getOrderAmount(@PathVariable("id") int id){
        OrderResponse orderResponse = orderService.getOrderById(id);
        return orderResponse.totalAmount();
    }
}


