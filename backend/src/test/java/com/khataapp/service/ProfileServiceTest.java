package com.khataapp.service;

import com.khataapp.dto.ProfileUpdateRequest;
import com.khataapp.exception.ConflictException;
import com.khataapp.model.KhataItem;
import com.khataapp.repository.KhataRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class ProfileServiceTest {
    @Mock
    private KhataRepository repository;
    @Mock
    private PasswordEncoder encoder;
    @InjectMocks
    private ProfileService service;

    @Test
    void demoUserCannotChangeProfileFields() {
        KhataItem demo = new KhataItem();
        demo.setRole("DEMO");
        when(repository.findUserById("demo-id")).thenReturn(Optional.of(demo));

        assertThrows(ConflictException.class, () -> service.update("demo-id",
                new ProfileUpdateRequest("Changed", "changed", "changed@example.com", "Password1!")));
    }
}
