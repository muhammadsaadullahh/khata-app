package com.khataapp.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import jakarta.validation.constraints.Pattern;

public record RegisterRequest(
        @NotBlank @Size(max = 120) String fullName,
        @NotBlank @Size(min = 3, max = 50)
        @Pattern(regexp = "^[a-zA-Z0-9._-]+$", message = "Username contains invalid characters")
        String username,
        @NotBlank @Size(min = 8, max = 72) String password,
        @NotBlank @Email @Size(max = 254) String email) {
}
