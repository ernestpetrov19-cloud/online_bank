package com.example.online_bank.config;

import com.example.online_bank.security.jwt.service.SecretKeyManager;
import lombok.RequiredArgsConstructor;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
@RequiredArgsConstructor
class JwtConfigTest {
    @InjectMocks
    private JwtConfig jwtConfig;
    @Mock
    SecretKeyManager secretKeyManager;


}