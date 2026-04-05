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

import java.util.Date;
import java.util.List;
import java.util.Map;

import static com.example.online_bank.enums.TokenType.ACCESS;

@Component
@RequiredArgsConstructor
@Slf4j
public class AccessTokenFactory implements TokenFactory {
    private final JwtConfig config;
    private final JwtService jwtService;

    /**
     * Создает access токен.
     * Проверяет тип токена.
     * Создает даты
     */
    @Override
    public String createToken(TokenType type, UserContainer userContainer) {
        log.info("Create access token");

        log.info("Создание дат");
        Date issuedDate = new Date();
        // Date notBeforeDate = new Date(issuedDate.getTime() + config.getNotBeforeTime().toMillis());
        Date notBeforeDate = issuedDate;
        Date expiredDate = new Date(issuedDate.getTime() + config.getAccessTokenLifetime().toMillis());

        log.info("Получение uuid пользователя");
        String subject = userContainer.uuid();
        log.trace("Получение ролей пользователя");
        List<String> subjectRoles = userContainer.roles();

        Map<String, Object> claims = jwtService.createClaims();

        log.info("Помещаем значения в клаймы");
        claims.put("roles", subjectRoles);
        claims.put("token_type", type);
        claims.put("name", userContainer.name());

        String id = jwtService.createUuid();

        log.info("Собираем токен");
        String token = Jwts.builder()
                .subject(subject)
                .issuer(config.getIssuer())
                .id(id)
                .notBefore(notBeforeDate)
                .expiration(expiredDate)
                .signWith(config.getKey())
                .audience().add(config.getAudience())
                .and()
                .claims(claims)
                .issuedAt(issuedDate)
                .compact();
        System.out.println(token);

        //todo сделать тестовые логи
        log.info("access token created {}", token);
        return token;
    }

    /**
     * @param supported
     * @return
     */
    @Override
    public boolean supports(TokenType supported) {
        return supported == ACCESS;
    }
}