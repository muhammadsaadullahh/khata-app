package com.khataapp.service;

import com.khataapp.dto.AuthResponse;
import com.khataapp.dto.LoginRequest;
import com.khataapp.dto.RegisterRequest;
import com.khataapp.dto.UserResponse;
import com.khataapp.exception.ConflictException;
import com.khataapp.exception.InvalidCredentialsException;
import com.khataapp.model.ItemType;
import com.khataapp.model.KhataItem;
import com.khataapp.repository.KhataRepository;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.util.Locale;
import java.util.UUID;

@Service
public class AuthService {
    private final KhataRepository repository;
    private final PasswordEncoder passwordEncoder;
    private final JwtService jwtService;

    public AuthService(KhataRepository repository, PasswordEncoder passwordEncoder, JwtService jwtService) {
        this.repository = repository;
        this.passwordEncoder = passwordEncoder;
        this.jwtService = jwtService;
    }

    public AuthResponse register(RegisterRequest request) {
        String username = normalize(request.username());
        if (repository.findUserByUsername(username).isPresent()) {
            throw new ConflictException("Username is already registered");
        }

        String userId = UUID.randomUUID().toString();
        String createdAt = Instant.now().toString();
        KhataItem user = new KhataItem();
        user.setPk("USER#" + userId);
        user.setSk("METADATA");
        user.setItemType(ItemType.USER.name());
        user.setUserId(userId);
        user.setFullName(request.fullName().trim());
        user.setUsername(username);
        user.setPasswordHash(passwordEncoder.encode(request.password()));
        user.setEmail(request.email().trim().toLowerCase(Locale.ROOT));
        user.setCreatedAt(createdAt);
        user.setCurrency(request.currency() == null || request.currency().isBlank() ? "INR" : request.currency().toUpperCase(Locale.ROOT));
        user.setRole(repository.hasUsers() ? "USER" : "OWNER");
        repository.saveUser(user);
        for (String category : new String[]{"Food", "Travel", "Bills", "Shopping", "Other"}) {
            KhataItem item = new KhataItem();
            item.setPk(user.getPk()); item.setSk("CATEGORY#" + UUID.randomUUID());
            item.setItemType(ItemType.CATEGORY.name()); item.setUserId(userId);
            item.setCategoryId(item.getSk().substring("CATEGORY#".length()));
            item.setCategory(category); item.setSystemCategory(true);
            repository.save(item);
        }
        return new AuthResponse(jwtService.generateToken(userId), toResponse(user));
    }

    public AuthResponse login(LoginRequest request) {
        String username = normalize(request.username());
        KhataItem user = repository.findUserByUsername(username)
                .orElseThrow(InvalidCredentialsException::new);
        if (!passwordEncoder.matches(request.password(), user.getPasswordHash())) {
            throw new InvalidCredentialsException();
        }
        return new AuthResponse(jwtService.generateToken(user.getUserId()), toResponse(user));
    }

    private UserResponse toResponse(KhataItem user) {
        return new UserResponse(user.getUserId(), user.getFullName(), user.getUsername(),
                user.getEmail(), user.getCreatedAt(), user.getCurrency() == null ? "INR" : user.getCurrency(), user.getRole() == null ? "USER" : user.getRole());
    }

    private String normalize(String value) {
        return value.trim().toLowerCase(Locale.ROOT);
    }
}
