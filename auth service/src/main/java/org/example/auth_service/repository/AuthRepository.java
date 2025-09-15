package org.example.auth_service.repository;

import org.example.auth_service.model.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;


@Repository
public interface AuthRepository extends JpaRepository<User,Integer> {
    Optional<User> findByUsername(String username);
    Boolean existsByEmail(String email);
}
