package org.example.orderservice.dto;

public record UserResponse(int id , String username, String email,String password, String role) {
}
