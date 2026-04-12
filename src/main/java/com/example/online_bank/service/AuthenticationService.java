package com.example.online_bank.service;


import com.example.online_bank.domain.dto.AuthenticationResponseDto;
import com.example.online_bank.domain.dto.RegenerateOtpDto;
import com.example.online_bank.domain.dto.VerificationRequest;
import com.example.online_bank.domain.entity.RefreshToken;
import com.example.online_bank.domain.entity.TokenFamily;
import com.example.online_bank.domain.entity.TrustedDevice;
import com.example.online_bank.domain.entity.User;
import com.example.online_bank.exception.UserAgentNotEqualException;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import static com.example.online_bank.enums.SecurityMessage.SECURITY_MESSAGE;
import static com.example.online_bank.enums.TokenStatus.REVOKED;

@Slf4j
@Service
@RequiredArgsConstructor
public class AuthenticationService {
    private final AuthenticationManager authenticationManager;
    private final TokenService tokenService;
    private final UserService userService;
    private final TrustedDeviceService trustedDeviceService;
    private final RefreshTokenService refreshTokenService;
    private final TokenFamilyService tokenFamilyService;
    private final JwtService jwtService;
    private final DeviceService deviceService;
    private final UserAgentService userAgentService;
    private final VerificationService verificationService;
    private final VerifiedCodeService verifiedCodeService;

    /**
     * Первый вход
     * email
     * → OTP
     * → подтверждение OTP
     * → create TrustedDevice
     * → create TokenFamily
     * → create RefreshToken
     * → return access + refresh + deviceId
     */
    @Transactional
    public AuthenticationResponseDto firstVerification(VerificationRequest dtoRequest) {
        //5 создаем trusted_device_id
        return verificationService.checkVerifyCode(dtoRequest, checkIsVerify(dtoRequest));
    }

    @Transactional
    public AuthenticationResponseDto defaultVerification(VerificationRequest dtoRequest) {

        return verificationService.checkVerifyCode(dtoRequest, checkIsVerify(dtoRequest));
    }

    private boolean checkIsVerify(VerificationRequest dtoRequest) {
        return userService.findByEmail(dtoRequest.email()).orElseThrow(EntityNotFoundException::new).getIsVerified();
    }

    //Тихий вход (refresh rotation)
    //access expired
    //→ refresh
    //
    //1. refresh найден?
    //   нет → 401
    //
    //2. refresh.status == REVOKED ?
    //   → reuse detected
    //   → block TokenFamily
    //   → revoke ALL refresh in family
    //   → REQUIRE OTP
    //
    //3. family.isBlocked == true ?
    //   → REQUIRE OTP
    //
    //4. refresh.expiresAt < now ?
    //   → 401 (expired)
    //
    //5. OK:
    //   → revoke old refresh
    //   → create new refresh
    //   → return access + refresh
    @Transactional
    public AuthenticationResponseDto silentLogin(String refreshToken, String deviceId) {
        RefreshToken tokenByUuidHash = refreshTokenService.parseToken(refreshToken);
        jwtService.validateToken(refreshToken);
        TokenFamily family = tokenByUuidHash.getFamily();
        User user = family.getUser();

        deviceService.checkDeviceBinding(deviceId, family.getTrustedDevice());

        checkReuseDetection(tokenByUuidHash, family, deviceId, user);
        log.info("start revoke old  token");
        refreshTokenService.revoke(tokenByUuidHash);
        return tokenService.createTokenHelper(user, family);
    }

    private void checkReuseDetection(RefreshToken refreshTokenByTokenHash, TokenFamily family, String deviceId, User user) {
        if (refreshTokenByTokenHash.getStatus().equals(REVOKED)) {
            log.error("Reuse detected");
            tokenFamilyService.blockFamily(family);
            refreshTokenService.revokeAllByFamily(family);
            trustedDeviceService.deleteByUserAndDeviceId(deviceId, user);
            verifiedCodeService.regenerateOtp(new RegenerateOtpDto(user.getEmail()));
            throw new SecurityException(SECURITY_MESSAGE.getValue());
        }
    }

    @Transactional
    public void logout(String refreshToken, String deviceId) {
        RefreshToken tokenByUuidHash = refreshTokenService.parseToken(refreshToken);
        jwtService.validateToken(refreshToken);
        TokenFamily family = tokenByUuidHash.getFamily();

        checkReuseDetection(tokenByUuidHash, family, deviceId, family.getUser());
        tokenFamilyService.revokeTokenAndBlockFamily(family, tokenByUuidHash);
    }

    //и если устройство есть в списке доверенных и СРАВНИВАЕМ USERAGENT КОТОРЫЙ ПРИШЕЛ С ТЕМ, ЧТО ХРАНИТСЯ В БАЗЕ:,
    //
    // то пропускаем(выдаем токены) без лишних вопросов
    //если это новое устройство т.е. deviceId пришел пустым из localStorage,
    // то генерируем его сами и тогда запрашиваем код с почты, фронту в localStorage кладем этот deviceId в
    // качестве ответа,
    // тот вырисовывает форму с подтверждением пароля и когда код отправлен, то вместе с ним отправляем тот deviceId,
    // который получили вместе с ответом.
    //если окей, то добавляем устройство, создаем семью
    //Правильно: Если deviceId совпал, а UA немного отличается (версия браузера) — обнови UA в базе
    // и пусти пользователя.
    //Правильно: Если deviceId совпал, а UA кардинально отличается (был iPhone, стал Windows) — это тревога
    // (спуфинг/подмена),
    // тогда требуй 2FA или блокируй.

    //если доверенного устройства не нашлось, то тогда отправляем otp code на почту
    @Transactional
    public AuthenticationResponseDto login(String email, String password, String deviceId, String deviceName, String userAgent) {
        User user = userService.findByEmail(email)
                .orElseThrow(() -> new EntityNotFoundException("Пользователь не найден"));

        deviceService.checkDeviceId(email, deviceId, user);
        trustedDeviceService.checkExistTrustedDevice(email, deviceId, user);

        TrustedDevice trustedDevice = trustedDeviceService.findByDeviceIdAndUser_email(deviceId, email);

        if (userAgentService.checkUserAgent(userAgent, trustedDevice.getUserAgent())) {
            trustedDeviceService.updateUserAgent(userAgent, trustedDevice);
        } else {
            log.error("user agent not equal");
            verifiedCodeService.regenerateOtp(new RegenerateOtpDto(email));
            throw new UserAgentNotEqualException(SECURITY_MESSAGE.getValue());
        }

        //если пароль не совпал, то выкидываем ошибку
        authenticationManager.authenticate(new UsernamePasswordAuthenticationToken(email, password));

        TokenFamily tokenFamily = tokenFamilyService.createFamilyAndTrustedDevice(deviceName, deviceId, user, userAgent);
        return tokenService.createTokenHelper(user, tokenFamily);
    }
}