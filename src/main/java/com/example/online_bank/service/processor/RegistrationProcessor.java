package com.example.online_bank.service.processor;

import com.example.online_bank.domain.dto.RegistrationDtoRequest;
import com.example.online_bank.domain.entity.User;
import com.example.online_bank.domain.entity.VerifiedCode;
import com.example.online_bank.domain.event.SendOtpEvent;
import com.example.online_bank.exception.EntityAlreadyExistsException;
import com.example.online_bank.mapper.UserMapper;
import com.example.online_bank.service.RoleService;
import com.example.online_bank.service.UserService;
import com.example.online_bank.service.VerifiedCodeService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.function.TriFunction;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;

import static com.example.online_bank.enums.VerifiedCodeType.EMAIL;
import static com.example.online_bank.util.CodeGeneratorUtil.generateOtp;

@Service
@RequiredArgsConstructor
@Slf4j
public class RegistrationProcessor {
    private final BCryptPasswordEncoder bCryptPasswordEncoder;
    private final RoleService roleService;
    private final VerifiedCodeService verifiedCodeService;
    private final UserService userService;
    private final UserMapper userMapper;

    public SendOtpEvent register(
            RegistrationDtoRequest registrationDtoRequest,
            TriFunction<RegistrationDtoRequest, RoleService, BCryptPasswordEncoder, User> mapper) {

        if (userService.existsByPhoneNumber(registrationDtoRequest.phone())
                || userService.existsByEmail(registrationDtoRequest.email())) {
            log.warn("Номер или почта уже используется");
            throw new EntityAlreadyExistsException("Пользователь с таким номером или почтой уже зарегистрирован");
        }

        User user = mapper.apply(registrationDtoRequest, roleService, bCryptPasswordEncoder);
        userService.save(user);

        String code = generateOtp();
        LocalDateTime expireDate = verifiedCodeService.createExpirationDate(200);
        VerifiedCode verifiedCode = verifiedCodeService.createVerifiedCode(code, user, expireDate, EMAIL);
        verifiedCodeService.save(verifiedCode);

        log.trace("Завершение регистрации");
        return userMapper.toSendOtpEvent(registrationDtoRequest, code);
    }
}
