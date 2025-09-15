package org.example.auth_service.dto;

import org.example.auth_service.model.Role;

public record UserResponse(int id , String username, String email,String password, Role role) {
}
