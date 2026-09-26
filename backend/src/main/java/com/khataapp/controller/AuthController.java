package com.khataapp.controller;

import com.khataapp.dto.AuthResponse;
import com.khataapp.dto.LoginRequest;
import com.khataapp.dto.RegisterRequest;
import com.khataapp.service.AuthService;
import com.khataapp.dto.CurrencyUpdateRequest;
import com.khataapp.dto.UserResponse;
import com.khataapp.model.KhataItem;
import com.khataapp.repository.KhataRepository;
import org.springframework.security.core.Authentication;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;
import com.khataapp.dto.ProfileUpdateRequest;
import com.khataapp.service.ProfileService;

@RestController
@RequestMapping("/api/v1/auth")
public class AuthController {
    private final AuthService authService;
    private final KhataRepository repository;
    private final ProfileService profileService;

    public AuthController(AuthService authService, KhataRepository repository, ProfileService profileService) {
        this.authService = authService;
        this.repository = repository;
        this.profileService = profileService;
    }

    @PostMapping("/register")
    @ResponseStatus(HttpStatus.CREATED)
    public AuthResponse register(@Valid @RequestBody RegisterRequest request) {
        return authService.register(request);
    }

    @PostMapping("/login")
    public AuthResponse login(@Valid @RequestBody LoginRequest request) {
        return authService.login(request);
    }

    @org.springframework.web.bind.annotation.PutMapping("/me/preferences")
    public UserResponse updatePreferences(@Valid @RequestBody CurrencyUpdateRequest request, Authentication authentication) {
        KhataItem user = repository.findUserById(authentication.getName())
                .orElseThrow(() -> new com.khataapp.exception.NotFoundException("User not found"));
        user.setCurrency(request.currency());
        repository.save(user);
        return new UserResponse(user.getUserId(), user.getFullName(), user.getUsername(), user.getEmail(),
                user.getCreatedAt(), user.getCurrency(), user.getRole());
    }

    @org.springframework.web.bind.annotation.PutMapping("/me")
    public UserResponse updateProfile(@Valid @RequestBody ProfileUpdateRequest request, Authentication authentication) {
        return profileService.update(authentication.getName(), request);
    }
}
