package com.example.online_bank.service;

import com.example.online_bank.domain.dto.RegenerateVerifiedCodeDto;
import com.example.online_bank.domain.dto.VerificationResponseDto;
import com.example.online_bank.domain.entity.User;
import com.example.online_bank.domain.entity.VerificationCode;
import com.example.online_bank.domain.event.SendVerificationCodeEvent;
import com.example.online_bank.enums.CodeType;
import com.example.online_bank.service.domain.VerificationCodeService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Slf4j
@Service
@RequiredArgsConstructor
public class VerificationManager {
    private final UserService userService;
    private final VerificationCodeService verificationCodeService;
    private final ApplicationEventPublisher applicationEventPublisher;

    @Transactional
    public VerificationResponseDto verifyUserByEmail(String code, String email, CodeType codeType) {
        User user = userService.findByEmail(email);
        VerificationCode verificationCode = verificationCodeService.findCodeByUser(code, user, codeType);
        verificationCodeService.verifyCode(verificationCode);
        userService.verifyUser(user);
        verificationCodeService.deleteAllUserVerificationCodes(user.getId());
        return new VerificationResponseDto(user);
    }

    @Transactional
    public void regenerateVerificationCode(RegenerateVerifiedCodeDto dto) {
        VerificationCode oldVerificationCode = verificationCodeService.findCodeByUserEmail(dto.email());

        String newVerificationCode = verificationCodeService.updateVerificationCode(dto.email());
        var event = new SendVerificationCodeEvent(
                dto.email(),
                newVerificationCode,
                oldVerificationCode.getSubjectMessage(),
                oldVerificationCode.getBodyMessage()
        );
        applicationEventPublisher.publishEvent(event);
    }
}
