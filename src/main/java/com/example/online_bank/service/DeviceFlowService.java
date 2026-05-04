package com.example.online_bank.service;

import com.example.online_bank.domain.dto.LoginRequestDto;
import com.example.online_bank.domain.entity.User;
import com.example.online_bank.domain.entity.VerificationCode;
import com.example.online_bank.domain.event.SendVerificationCodeEvent;
import com.example.online_bank.service.domain.VerificationCodeService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import static com.example.online_bank.enums.BodyMessage.CONFIRM_LOGIN;
import static com.example.online_bank.enums.CodeType.EMAIL_AUTHENTICATION;
import static com.example.online_bank.enums.SubjectMessage.AUTHENTICATION;
import static org.springframework.transaction.annotation.Propagation.REQUIRES_NEW;

@Service
@RequiredArgsConstructor
@Slf4j
public class DeviceFlowService {
    private final DeviceChallengeService deviceChallengeService;
    private final VerificationCodeService verificationCodeService;
    private final ApplicationEventPublisher applicationEventPublisher;

    @Transactional(propagation = REQUIRES_NEW)
    public void handleNewUserDevice(LoginRequestDto loginRequest, User user) {
        log.info("Обнаружено новое устройство");
        deviceChallengeService.create(
                user,
                loginRequest.deviceName(),
                loginRequest.deviceId(),
                loginRequest.userAgent()
        );
        VerificationCode verificationCode = verificationCodeService.create(
                user,
                EMAIL_AUTHENTICATION,
                AUTHENTICATION,
                CONFIRM_LOGIN,
                false
        );
        SendVerificationCodeEvent event = new SendVerificationCodeEvent(
                loginRequest.email(),
                verificationCode.getVerificationCode(),
                AUTHENTICATION.getValue(),
                CONFIRM_LOGIN.getValue()
        );
        applicationEventPublisher.publishEvent(event);
    }
}
