package org.example.auth_service.service;


import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.example.auth_service.dto.*;
import org.example.auth_service.model.User;
import org.example.auth_service.repository.AuthRepository;
import org.example.auth_service.security.JwtUtil;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
@Slf4j
@RequiredArgsConstructor
public class AuthService {

    private final AuthRepository authRepository;
    private final PasswordEncoder passwordEncoder;
    private final AuthenticationManager authenticationManager;
    private final JwtUtil jwtUtil;

    public ResponseEntity<?> register(RegisterRequest registerRequest){
        User user = new User();
        user.setUsername(registerRequest.username());
        user.setEmail(registerRequest.email());
        user.setPassword(passwordEncoder.encode(registerRequest.password()));

        authRepository.save(user);

        String token = jwtUtil.generateToken(user.getId(),user.getUsername(),user.getRole());

        return ResponseEntity.ok(
                new RegisterResponse(registerRequest.username(),registerRequest.email(),user.getRole().name(),token));
    }

    public ResponseEntity<?> login(LoginRequest loginRequest){

        User user = authRepository.findByUsername(loginRequest.username())
                .orElseThrow(() -> new RuntimeException("Invalid username or password"));


        if (!passwordEncoder.matches(loginRequest.password(), user.getPassword())) {
            return ResponseEntity.badRequest().body("Invalid username or password");
        }

        String token = jwtUtil.generateToken(user.getId(),user.getUsername(),user.getRole());

        return ResponseEntity.ok(new LoginResponse(user.getUsername(),token));


    }

    public ResponseEntity<?> getUserByUsername(String username) {
        User user = authRepository.findByUsername(username)
                .orElseThrow(() -> new RuntimeException("Invalid username"));

        log.info("user {}",user);
        return ResponseEntity.ok(new UserResponse(user.getId(),user.getUsername(),user.getEmail(),user.getPassword(),user.getRole()));
    }

    public ResponseEntity<?> getUserById(String userId) {
        User user = authRepository.findById(Integer.parseInt(userId))
                .orElseThrow(() -> new RuntimeException("Invalid user id"));

        return ResponseEntity.ok(new UserResponse(user.getId(),user.getUsername(),user.getEmail(),user.getPassword(),user.getRole()));
    }
}
