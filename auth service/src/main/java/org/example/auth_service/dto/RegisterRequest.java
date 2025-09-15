package org.example.auth_service.dto;

public record RegisterRequest(String username, String email, String password) {
}
