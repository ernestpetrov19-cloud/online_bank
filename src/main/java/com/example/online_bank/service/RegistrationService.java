package com.example.online_bank.service;

import com.example.online_bank.domain.dto.RegistrationDtoRequest;
import com.example.online_bank.domain.event.SendOtpEvent;
import com.example.online_bank.mapper.UserMapper;
import com.example.online_bank.service.processor.RegistrationProcessor;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Slf4j
@Service
@RequiredArgsConstructor
public class RegistrationService {
    private final UserMapper userMapper;
    private final RegistrationProcessor registrationProcessor;
    private final EventPublisherService<SendOtpEvent> eventPublisherService;

    @Transactional
    public void signUp(RegistrationDtoRequest registrationDtoRequest) {
        SendOtpEvent event = registrationProcessor.register(registrationDtoRequest, userMapper::toUser);
        eventPublisherService.publishEvent(event);
    }

    @Transactional
    public void adminSignUp(RegistrationDtoRequest registrationDtoRequest) {
        SendOtpEvent event = registrationProcessor.register(registrationDtoRequest, userMapper::toAdmin);
        eventPublisherService.publishEvent(event);
    }
}