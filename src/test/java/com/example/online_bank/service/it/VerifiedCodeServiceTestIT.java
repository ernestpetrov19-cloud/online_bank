package com.example.online_bank.service.it;

import com.example.online_bank.OnlineBankApplication;
import com.example.online_bank.config.JwtConfig;
import com.example.online_bank.domain.entity.User;
import com.example.online_bank.domain.entity.VerifiedCode;
import com.example.online_bank.enums.VerifiedCodeType;
import com.example.online_bank.repository.VerifiedCodeRepository;
import com.example.online_bank.service.MailService;
import com.example.online_bank.service.UserService;
import com.example.online_bank.service.VerifiedCodeService;
import com.example.online_bank.service.impl.EmailNotificationServiceImpl;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

import static com.example.online_bank.util.CodeGeneratorUtil.generateOtp;
import static org.junit.jupiter.api.Assertions.*;


@RequiredArgsConstructor
@ContextConfiguration(classes = OnlineBankApplication.class)
@SpringBootTest(classes = OnlineBankApplication.class)
@Slf4j
class VerifiedCodeServiceTestIT {
    @Autowired
    private VerifiedCodeService verifiedCodeService;
    @Autowired
    private VerifiedCodeRepository verifiedCodeRepository;
    @Autowired
    private UserService userService;
    @MockBean
    private EmailNotificationServiceImpl emailNotificationService;
    @MockBean
    private MailService mailService;
    @MockBean
    private JwtConfig jwtConfig;

    @Test
    @Transactional
    @DisplayName("Успешное создание otp кода")
    void successCreateVerifiedCode() {
        //подготовка данных arr
        User userMock = User.builder().build();
        userService.save(userMock);

        VerifiedCode otpCodeEntity = verifiedCodeService.createVerifiedCode(
                "4444",
                userMock,
                verifiedCodeService.createExpirationDate(20),
                VerifiedCodeType.EMAIL);

        verifiedCodeService.save(otpCodeEntity);
        //act
        assertDoesNotThrow(() -> verifiedCodeRepository.findVerifiedCodeByVerifiedCode(otpCodeEntity.getVerifiedCode())
                .orElseThrow(EntityNotFoundException::new));

        assertNotNull(otpCodeEntity.getVerifiedCode());
        assertFalse(otpCodeEntity.getIsVerified());
        assertNotNull(otpCodeEntity.getCreatedAt());
        assertTrue(otpCodeEntity.getExpiresAt().isAfter(otpCodeEntity.getCreatedAt()));
    }

    @Test
    @Transactional
    @DisplayName("Успешное удаление всех истекших кодов")
    void successfulRemoveExpiredOtpCode() {
        verifiedCodeService.clearOldCodes();
        List<VerifiedCode> allOtp = verifiedCodeRepository.findAll();
        assertTrue(allOtp.isEmpty());
    }

    public void test() {
        System.out.println("Hello");
    }

    @Test
    @Transactional
    @DisplayName("Успешное удаление истекших кодов пользователя")
    void successfulRemoveExpiredOtpCodeByUser() {
        //подготовка данных arr
        User userMock = User.builder().build();
        userService.save(userMock);

        LocalDateTime now = LocalDateTime.now();
        VerifiedCode otpCodeEntity = verifiedCodeService.createVerifiedCode(
                generateOtp(),
                userMock,
                now,
                VerifiedCodeType.EMAIL);

        verifiedCodeService.save(otpCodeEntity);
        verifiedCodeService.cleanAllCodes(userMock.getId());
        //act
        assertTrue(verifiedCodeRepository.findAllByExpiresAtBeforeAndUser_Id(now, userMock.getId()).isEmpty());
    }

    @Test
    @DisplayName("Найти неистекший код для пользователя и поставить ему isVerified = true")
    @Transactional
    void validateCode() {
        //подготовка данных arr
        User userMock = User.builder()
                .isVerified(Boolean.FALSE)
                .build();
        userService.save(userMock);

        VerifiedCode otpCodeEntity = verifiedCodeService.createVerifiedCode(
                "7777",
                userMock,
                verifiedCodeService.createExpirationDate(20),
                VerifiedCodeType.EMAIL);

        verifiedCodeService.save(otpCodeEntity);
        //act
        VerifiedCode verifiedCode = assertDoesNotThrow(() -> verifiedCodeRepository.findVerifiedCodeByVerifiedCode("7777")
                .orElseThrow(EntityNotFoundException::new));
        log.debug(verifiedCode.toString());
        assertTrue(verifiedCode.getIsVerified());
    }
}