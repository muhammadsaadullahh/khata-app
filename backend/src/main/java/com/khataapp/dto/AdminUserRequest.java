package com.khataapp.dto;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;

public record AdminUserRequest(@Valid RegisterRequest account,
                               @NotBlank @Pattern(regexp = "^(USER|DEMO|ADMIN)$") String role) {
}
