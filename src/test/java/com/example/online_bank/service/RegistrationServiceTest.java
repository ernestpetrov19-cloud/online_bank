package com.example.online_bank.service;

import com.example.online_bank.domain.dto.RegistrationDtoRequest;
import com.example.online_bank.domain.event.SendOtpEvent;
import com.example.online_bank.exception.EntityAlreadyExistsException;
import com.example.online_bank.mapper.UserMapper;
import com.example.online_bank.repository.UserRepository;
import com.example.online_bank.service.processor.RegistrationProcessor;
import org.apache.commons.lang3.function.TriFunction;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class RegistrationServiceTest {
    @Mock
    private UserRepository userRepository;
    @Mock
    private UserService userService;
    @Mock
    private UserMapper userMapper;
    @Mock
    private BCryptPasswordEncoder bCryptPasswordEncoder;
    @Mock
    private RoleService roleService;
    @Mock
    private VerifiedCodeService verifiedCodeService;
    @Mock
    private RegistrationProcessor registrationProcessor;
    @Mock
    private EventPublisherService eventPublisherService;
    @InjectMocks
    private RegistrationService registrationService;

    @Test
    void successSignUp() {
        //arrange Подготовка данных
        RegistrationDtoRequest request = new RegistrationDtoRequest(
                "testName",
                "testSurname",
                "testPatronymic",
                "89608052696",
                "wass",
                "myemail@test.com"
        );

        SendOtpEvent mockEvent = new SendOtpEvent("myemail@test.com", "1234", "some text");

        when(registrationProcessor.register(Mockito.eq(request), any(TriFunction.class)))
                .thenReturn(mockEvent);
        Assertions.assertDoesNotThrow(() -> registrationService.signUp(request));

        verify(eventPublisherService).publishEvent(mockEvent);
    }

    @Test
    void failedSignUpByPhoneNumberAlreadyExists() {
        //arrange Подготовка данных
        RegistrationDtoRequest registrationDtoRequest = new RegistrationDtoRequest(
                "testName",
                "testSurname",
                "testPatronymic",
                "89608052696",
                "wass",
                "myemail@test.com"
        );
        when(registrationProcessor.register(any(RegistrationDtoRequest.class), any(TriFunction.class)))
                .thenThrow(EntityAlreadyExistsException.class);
        assertThrows(EntityAlreadyExistsException.class, () -> registrationService.signUp(registrationDtoRequest));
        verifyNoInteractions(eventPublisherService);
    }

    @Test
    void failedSignUpByEmailAlreadyExists() {
        //arrange Подготовка данных
        RegistrationDtoRequest request = new RegistrationDtoRequest(
                "testName",
                "testSurname",
                "testPatronymic",
                "89608052696",
                "wass",
                "myemail@test.com"
        );
        when(registrationProcessor.register(any(RegistrationDtoRequest.class), any(TriFunction.class)))
                .thenThrow(EntityAlreadyExistsException.class);
        assertThrows(EntityAlreadyExistsException.class, () -> registrationService.signUp(request));
        verifyNoInteractions(eventPublisherService);
    }
}