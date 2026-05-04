package com.example.online_bank.service.processor;

import com.example.online_bank.domain.dto.RegistrationDtoRequest;
import com.example.online_bank.domain.entity.User;
import com.example.online_bank.domain.entity.VerificationCode;
import com.example.online_bank.domain.event.SendVerificationCodeEvent;
import com.example.online_bank.exception.EntityAlreadyExistsException;
import com.example.online_bank.service.RoleService;
import com.example.online_bank.service.UserService;
import com.example.online_bank.service.domain.VerificationCodeService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.function.TriFunction;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

import static com.example.online_bank.enums.BodyMessage.VERIFICATION_BODY;
import static com.example.online_bank.enums.CodeType.EMAIL_VERIFICATION;
import static com.example.online_bank.enums.SubjectMessage.VERIFICATION;

@Service
@RequiredArgsConstructor
@Slf4j
public class RegistrationProcessor {
    private final BCryptPasswordEncoder bCryptPasswordEncoder;
    private final RoleService roleService;
    private final VerificationCodeService verificationCodeService;
    private final UserService userService;

    public SendVerificationCodeEvent register(
            RegistrationDtoRequest registrationDtoRequest,
            TriFunction<RegistrationDtoRequest, RoleService, BCryptPasswordEncoder, User> mapper
    ) {

        if (userService.existsByPhoneNumber(registrationDtoRequest.phone())
                || userService.existsByEmail(registrationDtoRequest.email())) {
            log.warn("Номер или почта уже используется");
            throw new EntityAlreadyExistsException("Пользователь с таким номером или почтой уже зарегистрирован");
        }

        User user = mapper.apply(registrationDtoRequest, roleService, bCryptPasswordEncoder);
        userService.save(user);

        VerificationCode verificationCode = verificationCodeService.create(
                user,
                EMAIL_VERIFICATION,
                VERIFICATION,
                VERIFICATION_BODY,
                false
        );

        log.trace("Завершение регистрации");
        return new SendVerificationCodeEvent(
                user.getEmail(),
                verificationCode.getVerificationCode(),
                VERIFICATION.getValue(),
                VERIFICATION_BODY.getValue());
    }
}
