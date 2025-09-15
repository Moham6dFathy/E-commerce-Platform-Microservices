package org.example.productservice.security;

import lombok.RequiredArgsConstructor;
import org.example.productservice.client.AuthClient;
import org.example.productservice.dto.UserResponse;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class CustomUserDetailsService implements UserDetailsService {

    private final AuthClient authClient;


    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        UserResponse user = authClient.getUserByUsername(username);

        return org.springframework.security.core.userdetails.User
                .withUsername(user.username())
                .password(user.password())
                .authorities(user.role())
                .build();
    }
}
