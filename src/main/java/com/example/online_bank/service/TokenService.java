package com.example.online_bank.service;

import com.example.online_bank.domain.dto.AuthenticationResponseDto;
import com.example.online_bank.domain.dto.UserContainer;
import com.example.online_bank.domain.entity.TokenFamily;
import com.example.online_bank.domain.entity.User;
import com.example.online_bank.mapper.UserMapper;
import com.example.online_bank.security.jwt.factory.impl.JwtFactoryOrchestrator;
import com.example.online_bank.security.jwt.factory.impl.RefreshTokenFactory;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import static com.example.online_bank.enums.TokenType.*;


@Service
@RequiredArgsConstructor
@Slf4j
public class TokenService {
    private final JwtFactoryOrchestrator jwtFactoryOrchestrator;
    private final RefreshTokenFactory refreshTokenFactory;
    private final UserMapper userMapper;
    private final RefreshTokenService refreshTokenService;
    private static final String CREATED_AT = "createdAt";
    private static final String EXPIRED_AT = "expiredAt";

    public String getAccessToken(UserContainer userContainer) {
        return jwtFactoryOrchestrator.createJwt(ACCESS, userContainer);
    }

    public String getIdToken(UserContainer userContainer) {
        return jwtFactoryOrchestrator.createJwt(ID, userContainer);
    }

    public String getRefreshToken(UserContainer userContainer) {
        return jwtFactoryOrchestrator.createJwt(REFRESH, userContainer);
    }

    public Map<String, Object> getRefreshTokenWithDate(UserContainer userContainer) {
        return refreshTokenFactory.createRefreshToken(REFRESH, userContainer);
    }

    public LocalDateTime getTime(String timeType, Map<String, Object> map) {
        return LocalDateTime.ofInstant(
                ((Date) map.get(timeType)).toInstant(),
                ZoneId.systemDefault()
        );
    }

    public AuthenticationResponseDto createAccessAndIdTokens(UserContainer userContainer) {
        log.info("Создание токенов");
        String accessToken = getAccessToken(userContainer);
        String idToken = getIdToken(userContainer);
        Map<String, String> tokens = new HashMap<>();
        tokens.put("accessToken", accessToken);
        tokens.put("idToken", idToken);
        return new AuthenticationResponseDto(tokens);
    }


    public AuthenticationResponseDto createTokenHelper(User user, TokenFamily tokenFamily) {
        //Если пароль правильный, то создать новую семью с переданным устройством и refresh токен
        //3. конвертируем в userContainer
        UserContainer userContainer = userMapper.toUserContainer(user);

        //создаем access и id
        AuthenticationResponseDto tokens = createAccessAndIdTokens(userContainer);
        log.info("tokens {}", tokens);
        //создаем refresh
        Map<String, Object> refreshAndDateMap = getRefreshTokenWithDate(userContainer);

        String refreshToken = (String) refreshAndDateMap.get("token");
        LocalDateTime expiredAt = getTime(EXPIRED_AT, refreshAndDateMap);
        LocalDateTime createdAt = getTime(CREATED_AT, refreshAndDateMap);

        // TokenFamily tokenFamily = createFamilyAndTrustedDevice(deviceName, deviceId, user, userAgent);
        refreshTokenService.createRefreshTokenEntity(refreshToken, tokenFamily, expiredAt, createdAt);
        putRefreshTokenToResponse(tokens, refreshToken);
        log.info("tokens {}", tokens);
        return tokens;
    }

    private void putRefreshTokenToResponse(AuthenticationResponseDto tokens, String refreshToken) {
        tokens.tokens().put("refreshToken", refreshToken);
    }


}