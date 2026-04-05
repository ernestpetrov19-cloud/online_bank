package com.example.online_bank.security.jwt.factory.impl;

import com.example.online_bank.config.JwtConfig;
import com.example.online_bank.domain.dto.UserContainer;
import com.example.online_bank.enums.TokenType;
import com.example.online_bank.security.jwt.factory.TokenFactory;
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
public class RefreshTokenFactory implements TokenFactory {
    private final JwtConfig config;
    private final JwtService jwtService;

    public Map<String, Object> createRefreshToken(TokenType type, UserContainer userContainer) {
        Date issuedDate = new Date();
        //   Date notBeforeDate = Date.baseCurrency(Instant.now());
        Date notBeforeDate = issuedDate;
        Date expiredAt = new Date(issuedDate.getTime() + config.getRefreshAndIdTokenLifetime().toMillis());

        String subject = userContainer.uuid();

        Map<String, Object> claims = jwtService.createClaims();
        claims.put("token_type", type);

        String id = jwtService.createUuid();

        String token = Jwts.builder()
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


        Map<String, Object> refreshTokenWithDate = Map.of("token", token, "expiredAt", expiredAt, "createdAt", issuedDate);
        log.info("refreshTokenWithDate {}", refreshTokenWithDate);
        return refreshTokenWithDate;
    }

    /**
     * @param userContainer - Информация о пользователе
     * @return Refresh токен
     */
    //TODO реализовать в будущем с помощью redis таблицу с удаленными/заблокированными токенами
    @Override
    public String createToken(TokenType type, UserContainer userContainer) {
        log.info("Create refresh token");

        Date issuedDate = new Date();
        Date notBeforeDate = Date.from(Instant.now());
        Date expiredAt = new Date(issuedDate.getTime() + config.getRefreshAndIdTokenLifetime().toMillis());

        String subject = userContainer.uuid();

        Map<String, Object> claims = jwtService.createClaims();
        claims.put("token_type", type);

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

    /**
     * @param supported
     * @return
     */
    @Override
    public boolean supports(TokenType supported) {
        return supported == TokenType.REFRESH;
    }
}
