package com.example.auth_service.dto;

import jakarta.annotation.Nullable;
import lombok.NonNull;

public record LoginRequest(
        @NonNull String username,
        @NonNull String password
) {

}
