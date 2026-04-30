package com.example.online_bank.security.jwt.factory.impl;

import com.example.online_bank.config.JwtConfig;
import com.example.online_bank.domain.dto.UserContainer;
import com.example.online_bank.enums.TokenType;
import com.example.online_bank.security.jwt.factory.TokenProvider;
import com.example.online_bank.service.JwtService;
import io.jsonwebtoken.Jwts;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.time.Instant;
import java.util.Date;
import java.util.Map;

@Component
@RequiredArgsConstructor
@Slf4j
public class RefreshTokenProvider implements TokenProvider {
    public static final String CLAIM_TYPE = "token_type";
    private final JwtConfig config;
    private final JwtService jwtService;

    @Override
    public String create(TokenType type, UserContainer userContainer) {

        Date issuedDate = new Date();
        Date notBeforeDate = Date.from(Instant.now());
        Date expiredAt = new Date(issuedDate.getTime() + config.getRefreshTokenLifetime().toMillis());

        String subject = userContainer.uuid();

        Map<String, Object> claims = jwtService.createClaims();
        claims.put(CLAIM_TYPE, type);

        String id = jwtService.createUuid();

        return Jwts.builder()
                .subject(subject)
                .issuer(config.getIssuer())
                .id(id)
                .notBefore(notBeforeDate)
                .expiration(expiredAt)
                .signWith(config.getKey())
                .audience().add(config.getAudience())
                .and()
                .claims(claims)
                .issuedAt(issuedDate)
                .compact();
    }

    @Override
    public boolean supports(TokenType supported) {
        return supported == TokenType.REFRESH;
    }
}
