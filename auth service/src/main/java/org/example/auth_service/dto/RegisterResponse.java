package org.example.auth_service.dto;

public record RegisterResponse(String username,String email,String role, String accessToken) {
}
