package com.example.online_bank.service;

import com.example.online_bank.domain.dto.UserContainer;
import com.example.online_bank.security.jwt.factory.impl.TokenResolverOrchestrator;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;
import java.util.UUID;

@ExtendWith(MockitoExtension.class)
class TokenServiceTest {
    @InjectMocks
    private TokenService tokenService;
    @Mock
    private TokenResolverOrchestrator jwtFactoryOrchestrator;

    @Test
    void successGetAccessToken() {
        UserContainer userContainer = new UserContainer(UUID.randomUUID().toString(), "test", List.of("ROLE_USER"));
        Assertions.assertDoesNotThrow(() -> {
            tokenService.getAccessToken(userContainer);
            tokenService.getRefreshToken(userContainer);
            tokenService.getIdToken(userContainer);
        });
    }

}