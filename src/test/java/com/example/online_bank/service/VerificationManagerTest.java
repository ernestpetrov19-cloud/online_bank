package com.example.online_bank.service;

import com.example.online_bank.domain.dto.RegenerateVerifiedCodeDto;
import com.example.online_bank.domain.dto.VerificationRequestDto;
import com.example.online_bank.domain.dto.VerificationResponseDto;
import com.example.online_bank.domain.entity.User;
import com.example.online_bank.domain.entity.VerificationCode;
import com.example.online_bank.domain.event.SendVerificationCodeEvent;
import com.example.online_bank.service.domain.VerificationCodeService;
import jakarta.persistence.EntityNotFoundException;
import lombok.SneakyThrows;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.context.ApplicationEventPublisher;

import java.time.LocalDateTime;

import static com.example.online_bank.enums.BodyMessage.VERIFICATION_BODY;
import static com.example.online_bank.enums.CodeType.EMAIL_VERIFICATION;
import static com.example.online_bank.enums.SubjectMessage.VERIFICATION;
import static java.time.LocalDateTime.of;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class VerificationManagerTest {
    @InjectMocks
    private VerificationManager verificationManager;
    @Mock
    private VerificationCodeService verificationCodeService;
    @Mock
    private ApplicationEventPublisher applicationEventPublisher;
    @Mock
    private UserService userService;

    private static final String OTP = "1234";
    private final User userMock = User.builder()
            .id(1L)
            .email("test@gmail.com")
            .phoneNumber("79999999999")
            .isVerified(false)
            .build();
    private final LocalDateTime createdAt = of(
            2026,
            4,
            15,
            12,
            0
    );
    private final LocalDateTime expAt = of(
            2026,
            4,
            15,
            12,
            0
    );
    private final VerificationCode verificationCodeMock = VerificationCode.builder()
            .verificationCode(OTP)
            .user(userMock)
            .isVerified(false)
            .codeType(EMAIL_VERIFICATION)
            .createdAt(createdAt)
            .expiresAt(expAt)
            .build();
    private final VerificationRequestDto verificationRequestDto = new VerificationRequestDto(
            "test@gmail.com",
            OTP,
            "Windows PC",
            "Chrome v.1234 Windows",
            null
    );
    private final RegenerateVerifiedCodeDto regenerateVerifiedCodeDto = new RegenerateVerifiedCodeDto("test@gmail.com");

    @Test
    @SneakyThrows
    void successVerifyUserByEmail() {
        when(userService.findByEmail(userMock.getEmail())).thenReturn(userMock);
        when(verificationCodeService.findCodeByUser(OTP, userMock, EMAIL_VERIFICATION))
                .thenReturn(verificationCodeMock);

        VerificationResponseDto verificationResponseDto = verificationManager.verifyUserByEmail(
                verificationRequestDto.verificationCode(),
                userMock.getEmail(),
                EMAIL_VERIFICATION
        );
        assertNotNull(verificationResponseDto);
        assertEquals("test@gmail.com", verificationResponseDto.verifiedUser().getEmail());
        verify(verificationCodeService).findCodeByUser(eq(OTP), eq(userMock), eq(EMAIL_VERIFICATION));
        verify(verificationCodeService).deleteAllUserVerificationCodes(anyLong());
    }

    @Test
    @SneakyThrows
    void failureVerifyUserByEmail() {
        when(userService.findByEmail(userMock.getEmail())).thenThrow(EntityNotFoundException.class);
        assertThrows(
                EntityNotFoundException.class,
                () -> verificationManager.verifyUserByEmail(
                        verificationRequestDto.verificationCode(),
                        userMock.getEmail(),
                        EMAIL_VERIFICATION)
        );

        Mockito.verifyNoMoreInteractions(verificationCodeService, applicationEventPublisher);
    }

    @Test
    void successRegenerateVerificationCode() {
        VerificationCode oldVerificationCode = VerificationCode.builder()
                .bodyMessage(VERIFICATION_BODY.getValue())
                .subjectMessage(VERIFICATION.getValue())
                .codeType(EMAIL_VERIFICATION)
                .isVerified(false)
                .verificationCode("1234")
                .build();

        String newVerificationCode = "0123";
        when(verificationCodeService.findCodeByUserEmail(regenerateVerifiedCodeDto.email()))
                .thenReturn(oldVerificationCode);
        var event = new SendVerificationCodeEvent(
                regenerateVerifiedCodeDto.email(),
                newVerificationCode,
                VERIFICATION.getValue(),
                VERIFICATION_BODY.getValue()
        );
        when(verificationCodeService.updateVerificationCode(eq(regenerateVerifiedCodeDto.email())))
                .thenReturn(newVerificationCode);
        doNothing().when(applicationEventPublisher).publishEvent(eq(event));
        verificationManager.regenerateVerificationCode(regenerateVerifiedCodeDto);
        verify(verificationCodeService).updateVerificationCode(regenerateVerifiedCodeDto.email());
        verify(applicationEventPublisher).publishEvent(event);
    }

    @Test
    void failureRegenerateVerificationCode() {
        when(verificationCodeService.findCodeByUserEmail(regenerateVerifiedCodeDto.email()))
                .thenThrow(EntityNotFoundException.class);
        assertThrows(EntityNotFoundException.class,
                () -> verificationManager.regenerateVerificationCode(regenerateVerifiedCodeDto));

        Mockito.verifyNoInteractions(applicationEventPublisher);
    }
}