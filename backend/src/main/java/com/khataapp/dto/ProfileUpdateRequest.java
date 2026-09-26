package com.khataapp.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;

public record ProfileUpdateRequest(
        @Size(max = 120) String fullName,
        @Size(min = 3, max = 50) @Pattern(regexp = "^[a-zA-Z0-9._-]+$") String username,
        @Email @Size(max = 254) String email,
        @Size(min = 8, max = 72) String password) {}
