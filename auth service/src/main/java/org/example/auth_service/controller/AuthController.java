package org.example.auth_service.controller;

import lombok.RequiredArgsConstructor;
import org.example.auth_service.dto.LoginRequest;
import org.example.auth_service.dto.LoginResponse;
import org.example.auth_service.dto.RegisterRequest;
import org.example.auth_service.dto.RegisterResponse;
import org.example.auth_service.model.User;
import org.example.auth_service.security.JwtUtil;
import org.example.auth_service.service.AuthService;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/auth")
@RequiredArgsConstructor
public class AuthController {

    private final AuthService authService;


    @PostMapping("/register")
    public ResponseEntity<?> register(@RequestBody RegisterRequest registerRequest) {
        return authService.register(registerRequest);
    }

    @PostMapping("/login")
    public ResponseEntity<?> login(@RequestBody LoginRequest loginRequest) {
        return authService.login(loginRequest);
    }

    @GetMapping("/user")
    public ResponseEntity<?> getUserByUsername(@RequestParam(value = "username" , required = false) String username,@RequestParam(value = "userId" , required = false) String userId) {
        if(userId != null){
            return authService.getUserById(userId);
        }
        return authService.getUserByUsername(username);
    }
}
