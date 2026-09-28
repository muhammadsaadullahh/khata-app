package com.khataapp.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;

public record ProfileUpdateRequest(
        @Size(max = 120) String fullName,
        @Size(min = 3, max = 50) @Pattern(regexp = "^[a-zA-Z0-9._-]+$") String username,
        @Email @Size(max = 254)
        @Pattern(regexp = "^[A-Za-z0-9.!#$%&'*+/=?^_`{|}~-]+@[A-Za-z0-9](?:[A-Za-z0-9-]{0,61}[A-Za-z0-9])?(?:\\.[A-Za-z0-9](?:[A-Za-z0-9-]{0,61}[A-Za-z0-9])?)+$",
                message = "Email must be a valid address") String email,
        @Size(min = 8, max = 72) String password) {}
