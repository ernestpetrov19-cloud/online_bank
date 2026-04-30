package com.example.online_bank.service;

import com.example.online_bank.domain.dto.RegistrationDtoRequest;
import com.example.online_bank.domain.event.SendVerificationCodeEvent;
import com.example.online_bank.mapper.UserMapper;
import com.example.online_bank.service.processor.RegistrationProcessor;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Service;

@Slf4j
@Service
@RequiredArgsConstructor
public class RegistrationService {
    private final UserMapper userMapper;
    private final RegistrationProcessor registrationProcessor;
    private final ApplicationEventPublisher applicationEventPublisher;

    @Transactional
    public void signUp(RegistrationDtoRequest registrationDtoRequest) {
        SendVerificationCodeEvent event = registrationProcessor.register(registrationDtoRequest, userMapper::toUser);
        applicationEventPublisher.publishEvent(event);
    }

    @Transactional
    public void adminSignUp(RegistrationDtoRequest registrationDtoRequest) {
        SendVerificationCodeEvent event = registrationProcessor.register(registrationDtoRequest, userMapper::toAdmin);
        applicationEventPublisher.publishEvent(event);
    }
}