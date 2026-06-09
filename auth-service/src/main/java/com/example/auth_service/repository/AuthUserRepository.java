package com.example.auth_service.repository;

import com.example.auth_service.entity.AuthUser;
import org.springframework.data.repository.CrudRepository;

import java.util.Optional;

public interface AuthUserRepository extends CrudRepository<AuthUser, Long> {
    Optional<AuthUser> findAuthUsersByUsername(String username);
}
