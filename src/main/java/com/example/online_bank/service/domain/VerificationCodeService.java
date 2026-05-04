package com.example.online_bank.service.domain;

import com.example.online_bank.domain.entity.User;
import com.example.online_bank.domain.entity.VerificationCode;
import com.example.online_bank.enums.BodyMessage;
import com.example.online_bank.enums.CodeType;
import com.example.online_bank.enums.SubjectMessage;
import com.example.online_bank.exception.VerificationOtpException;
import com.example.online_bank.repository.VerificationCodeRepository;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

import static com.example.online_bank.enums.VerifiedCodeProperty.EXP_DATE;
import static com.example.online_bank.util.CodeGeneratorUtil.generateVerificationCode;
import static java.util.UUID.randomUUID;

@Slf4j
@Service
@RequiredArgsConstructor
public class VerificationCodeService {
    private final VerificationCodeRepository verificationCodeRepository;

    public VerificationCode create(User user, CodeType type, SubjectMessage subjectMessage, BodyMessage bodyMessage, boolean isVerified) {
        String verificationCode = generateVerificationCode();

        LocalDateTime now = LocalDateTime.now();
        LocalDateTime expirationDate = createExpirationDate(EXP_DATE.getSeconds(), now);

        VerificationCode verificationCodeEntity = VerificationCode.builder()
                .id(randomUUID())
                .expiresAt(expirationDate)
                .verificationCode(verificationCode)
                .createdAt(LocalDateTime.now())
                .user(user)
                .isVerified(isVerified)
                .codeType(type)
                .subjectMessage(subjectMessage.getValue())
                .bodyMessage(bodyMessage.getValue())
                .build();

        verificationCodeRepository.save(verificationCodeEntity);

        return verificationCodeEntity;
    }

    private LocalDateTime createExpirationDate(int seconds, LocalDateTime now) {
        return now.plusSeconds(seconds);
    }

    private LocalDateTime createExpirationDate(int seconds) {
        return LocalDateTime.now().plusSeconds(seconds);
    }

    public void deleteAllUserVerificationCodes(Long userId) {
        verificationCodeRepository.deleteAllByIsVerifiedTrueAndUser_id(userId);
    }

    /**
     * Очистка всех старых кодов
     */
    public void clearOldCodes() {
        List<VerificationCode> oldCodes = verificationCodeRepository.findAllByExpiresAtBefore(LocalDateTime.now());
        verificationCodeRepository.deleteAll(oldCodes);
    }

    /**
     * Ищет код.
     *
     * @throws VerificationOtpException - в случае если код не был найден, где текущая дата меньше истечения даты истечения
     */
    public VerificationCode findCodeByUser(String code, User user, CodeType type) {
        LocalDateTime now = LocalDateTime.now();
        return verificationCodeRepository
                .findByVerificationCodeAndUser_IdAndCodeTypeAndIsVerifiedIsFalseAndExpiresAtAfter(
                        code, user.getId(), type, now)
                .orElseThrow(() ->
                        new VerificationOtpException("Ошибка верификации. Запросите новый код."));
    }

    public String updateVerificationCode(String email) {
        String newVerificationCode = generateVerificationCode();
        LocalDateTime newExpirationDate = createExpirationDate(EXP_DATE.getSeconds());
        verificationCodeRepository.updateVerifiedCodeByUser_Email(email, newVerificationCode, newExpirationDate);
        return newVerificationCode;
    }

    public void verifyCode(VerificationCode verificationCode) {
        verificationCode.setIsVerified(true);
        verificationCodeRepository.save(verificationCode);
    }

    public VerificationCode findCodeByUserEmail(String email) {
        return verificationCodeRepository.findByUser_Email(email)
                .orElseThrow(()-> new EntityNotFoundException("Пользователь с таким email не найден"));
    }
}