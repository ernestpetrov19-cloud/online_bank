package com.example.online_bank.service;

import com.example.online_bank.domain.dto.AuthenticationResponseDto;
import com.example.online_bank.domain.dto.VerificationRequest;
import com.example.online_bank.domain.entity.TokenFamily;
import com.example.online_bank.domain.entity.User;
import com.example.online_bank.exception.VerificationOtpException;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.stereotype.Service;

@Slf4j
@Service
@RequiredArgsConstructor
public class VerificationService {
    private final UserService userService;
    private final DeviceService deviceService;
    private final TokenFamilyService tokenFamilyService;
    private final UserQuestService userQuestService;
    private final TokenService tokenService;

    /**
     * //После верификации в любом случае инициализируем пользователя и статистику.
     * //и нового пользователя соединить со всеми квестами в текущем месяце.
     */
    public AuthenticationResponseDto checkVerifyCode(VerificationRequest dto, boolean isVerified) {
        try {
            // 1. Находим пользователя по email
            User user = userService.findByEmail(dto.email())
                    .orElseThrow(EntityNotFoundException::new);

            //2 сверяем otp code
            userService.verifyEmailCode(user, dto.code(), isVerified);
            //  log.info("Очистка старых кодов");

            // verifiedCodeService.cleanAllCodes(user.getId());

            String checkedDeviceId = deviceService.getOrCreateDeviceId(dto.deviceId());

            //создаем refresh
            TokenFamily tokenFamily = tokenFamilyService.createFamilyAndTrustedDevice(dto.deviceName(), checkedDeviceId, user, dto.userAgent());

            if (isVerified) {
                //hack делаю на первое время
                userQuestService.makeRelationBetweenUserAndQuest(user);
            }

            return tokenService.createTokenHelper(user, tokenFamily);
        } catch (VerificationOtpException e) {
            log.error(e.getMessage());
            throw new BadCredentialsException("Неверные учетные данные");
        }
    }
}
