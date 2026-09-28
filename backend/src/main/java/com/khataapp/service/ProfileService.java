package com.khataapp.service;

import com.khataapp.dto.ProfileUpdateRequest;
import com.khataapp.dto.UserResponse;
import com.khataapp.exception.ConflictException;
import com.khataapp.exception.NotFoundException;
import com.khataapp.model.KhataItem;
import com.khataapp.repository.KhataRepository;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import java.util.Locale;

@Service
public class ProfileService {
    private final KhataRepository repository;
    private final PasswordEncoder encoder;
    public ProfileService(KhataRepository repository, PasswordEncoder encoder) { this.repository = repository; this.encoder = encoder; }
    public UserResponse update(String id, ProfileUpdateRequest request) {
        KhataItem user = repository.findUserById(id).orElseThrow(() -> new NotFoundException("User not found"));
        if ("DEMO".equalsIgnoreCase(user.getRole())) {
            throw new ConflictException("Demo accounts can only update currency");
        }
        if (request.username() != null && !request.username().isBlank() && !request.username().equalsIgnoreCase(user.getUsername())
                && repository.findUserByUsername(request.username().trim().toLowerCase(Locale.ROOT)).isPresent())
            throw new ConflictException("Username is already registered");
        if (request.fullName() != null && !request.fullName().isBlank()) user.setFullName(request.fullName().trim());
        if (request.username() != null && !request.username().isBlank()) {
            String next = request.username().trim().toLowerCase(Locale.ROOT);
            if (!next.equals(user.getUsername())) repository.saveUsernameLookup(next, id);
            user.setUsername(next);
        }
        if (request.email() != null && !request.email().isBlank()) user.setEmail(request.email().trim().toLowerCase(Locale.ROOT));
        if (request.password() != null && !request.password().isBlank()) user.setPasswordHash(encoder.encode(request.password()));
        repository.save(user);
        return new UserResponse(user.getUserId(), user.getFullName(), user.getUsername(), user.getEmail(), user.getCreatedAt(), user.getCurrency(), user.getRole());
    }
}
