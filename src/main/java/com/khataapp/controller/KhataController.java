package com.khataapp.controller;

import com.khataapp.dto.CreateTransactionRequest;
import com.khataapp.dto.SummaryResponse;
import com.khataapp.dto.TransactionResponse;
import com.khataapp.service.KhataService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;
import java.util.List;

@RestController
@RequestMapping("/api/v1/khata")
public class KhataController {
    private final KhataService service;

    public KhataController(KhataService service) { this.service = service; }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public TransactionResponse create(@Valid @RequestBody CreateTransactionRequest request,
                                      Authentication authentication) {
        requireOwner(authentication, request.userId());
        return service.create(request);
    }

    @GetMapping("/{userId}")
    public List<TransactionResponse> findAll(@PathVariable String userId, Authentication authentication) {
        requireOwner(authentication, userId);
        return service.findAll(userId);
    }

    @GetMapping("/{userId}/summary")
    public SummaryResponse summary(@PathVariable String userId, Authentication authentication) {
        requireOwner(authentication, userId);
        return service.summary(userId);
    }

    private void requireOwner(Authentication authentication, String userId) {
        if (authentication == null || !userId.equals(authentication.getName())) {
            throw new org.springframework.security.access.AccessDeniedException("User does not own this khata");
        }
    }
}
