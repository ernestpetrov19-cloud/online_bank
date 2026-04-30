package com.example.online_bank.service;

import com.example.online_bank.domain.dto.UserContainer;
import com.example.online_bank.security.jwt.factory.impl.TokenResolverOrchestrator;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import static com.example.online_bank.enums.TokenType.*;

@Service
@RequiredArgsConstructor
@Slf4j
public class TokenService {
    private final TokenResolverOrchestrator jwtFactoryOrchestrator;

    public String getAccessToken(UserContainer userContainer) {
        return jwtFactoryOrchestrator.createJwt(ACCESS, userContainer);
    }

    public String getIdToken(UserContainer userContainer) {
        return jwtFactoryOrchestrator.createJwt(ID, userContainer);
    }

    public String getRefreshToken(UserContainer userContainer) {
        return jwtFactoryOrchestrator.createJwt(REFRESH, userContainer);
    }
}