package com.khataapp.controller;

import com.khataapp.dto.AdminUserRequest;
import com.khataapp.dto.UserResponse;
import com.khataapp.model.KhataItem;
import com.khataapp.repository.KhataRepository;
import com.khataapp.service.AuthService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/admin")
@PreAuthorize("hasAnyRole('OWNER','ADMIN')")
public class AdminController {
    private final AuthService authService;
    private final KhataRepository repository;

    public AdminController(AuthService authService, KhataRepository repository) {
        this.authService = authService;
        this.repository = repository;
    }

    @GetMapping("/users")
    public List<UserResponse> users() {
        return repository.findUsers().stream().map(this::toResponse).toList();
    }

    @PostMapping("/users")
    @ResponseStatus(HttpStatus.CREATED)
    public UserResponse create(@Valid @RequestBody AdminUserRequest request) {
        return authService.createManagedUser(request.account(), request.role());
    }

    private UserResponse toResponse(KhataItem user) {
        return new UserResponse(user.getUserId(), user.getFullName(), user.getUsername(),
                user.getEmail(), user.getCreatedAt(), user.getCurrency() == null ? "INR" : user.getCurrency(),
                user.getRole() == null ? "USER" : user.getRole());
    }
}
