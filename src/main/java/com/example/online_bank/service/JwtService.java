package com.example.online_bank.service;

import com.example.online_bank.config.JwtConfig;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.*;

@Service
@RequiredArgsConstructor
@Slf4j
public class JwtService {
    private final JwtConfig jwtConfig;

    /**
     * @return Создать UUID для токена
     */
    public String createUuid() {
        return UUID.randomUUID().toString();
    }

    /**
     * Создать клаймы для пользователя
     */
    public Map<String, Object> createClaims() {
        return new HashMap<>();
    }

    /**
     * @param token - JWT токен
     *              1) верифицируем токен через секретный ключ
     *              2) создаем jwt parser
     *              3)
     * @return получаем полезную нагрузку(клаймы из токена)
     */
    public Claims getPayload(String token) {
        return Jwts.parser()
                .verifyWith(jwtConfig.getKey())
                .build()
                .parseSignedClaims(token)
                .getPayload();
    }

    /**
     * Получаем роли из клаймов токена для spring authentication
     *
     * @param claims - клаймы токена
     * @return Роли пользователя
     */
    public Collection<? extends GrantedAuthority> mapRolesForSpringToken(Claims claims) {
        Object roles = claims.get("roles");
        if (roles == null) {
            throw new BadCredentialsException("Неверный токен");
        }

        if (!(roles instanceof List<?> roleList)) {

            log.error("Ошибка при попытке сделать каст клаймов в List. Ожидал - {}. Пришло - {}",
                    Collection.class.getName(), roles.getClass().getName());
            throw new BadCredentialsException("Неверный токен");
        }

        return roleList.stream()
                .map(role -> new SimpleGrantedAuthority((String) role))
                .toList();
    }

    /**
     * Получить клайм "name"
     *
     * @param claims клаймы токена
     * @return Имя пользователя
     * <p>
     */
    public String getUsername(Claims claims) {
        return claims.get("name", String.class);
    }

    /**
     * Получить subject(verifiedUser.uuid)
     *
     * @param claims клаймы токена
     * @return uuid пользователя
     */
    public String getSubject(Claims claims) {
        return claims.getSubject();
    }

    public String getUuid(Claims claims) {
        return claims.getId();
    }

    public LocalDateTime getExpDate(Claims claims) {
        return convertDateToLocalDateTime(claims.getExpiration());
    }

    public LocalDateTime getCreatedDate(Claims claims) {
        return convertDateToLocalDateTime(claims.getIssuedAt());
    }

    private LocalDateTime convertDateToLocalDateTime(Date date) {
        return LocalDateTime.ofInstant(
                date.toInstant(),
                ZoneId.systemDefault()
        );
    }
}
