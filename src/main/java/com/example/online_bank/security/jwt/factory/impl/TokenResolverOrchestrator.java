package com.example.online_bank.security.jwt.factory.impl;

import com.example.online_bank.domain.dto.UserContainer;
import com.example.online_bank.enums.TokenType;
import com.example.online_bank.exception.UnsupportedTokenTypeException;
import com.example.online_bank.security.jwt.factory.TokenProvider;
import com.example.online_bank.security.jwt.factory.TokenResolver;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.util.Set;

@Component
@RequiredArgsConstructor
@Slf4j
public class TokenResolverOrchestrator implements TokenResolver {
    private final Set<TokenProvider> tokenFactories;

    @Override
    public String createJwt(TokenType tokenType, UserContainer userContainer) {
        log.debug("Create JWT");
        return tokenFactories.stream()
                .filter(tokenProvider -> tokenProvider.supports(tokenType))
                .findFirst()
                .orElseThrow(() -> new UnsupportedTokenTypeException(tokenType))
                .create(tokenType, userContainer);
    }
}
