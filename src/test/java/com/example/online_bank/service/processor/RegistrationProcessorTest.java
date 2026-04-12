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
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

import java.time.LocalDateTime;

import static com.example.online_bank.enums.VerifiedCodeType.EMAIL;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@Slf4j
class RegistrationProcessorTest {
    @InjectMocks
    private RegistrationProcessor registrationProcessor;
    @Mock
    private BCryptPasswordEncoder bCryptPasswordEncoder;
    @Mock
    private RoleService roleService;
    @Mock
    private VerifiedCodeService verifiedCodeService;
    @Mock
    private UserService userService;
    @Mock
    private UserMapper userMapper;
    private final RegistrationDtoRequest request = new RegistrationDtoRequest(
            "testName",
            "testSurname",
            "testPatronymic",
            "89608052696",
            "wass",
            "myemail@test.com"
    );

    @Test
    void successRegisterToUser() {
        //arrange подготовка
        User userMock = User.builder()
                .name(request.name())
                .surname(request.surname())
                .phoneNumber(request.phone())
                .email(request.email())
                .build();

        VerifiedCode verifiedCodeMock = VerifiedCode.builder()
                .verifiedCode("1234")
                .user(userMock)
                .createdAt(LocalDateTime.now())
                .codeType(EMAIL)
                .build();

        SendOtpEvent expectedResult = new SendOtpEvent(request.email(), "1234", "some text");

        //обучение моков
        when(userService.existsByPhoneNumber(request.phone())).thenReturn(false);
        when(userService.existsByEmail(request.email())).thenReturn(false);
        when(userMapper.toUser(eq(request), any(), any())).thenReturn(userMock);
        when(verifiedCodeService.createVerifiedCode(anyString(), eq(userMock), any(), eq(EMAIL)))
                .thenReturn(verifiedCodeMock);
        when(userMapper.toSendOtpEvent(eq(request), anyString())).thenReturn(expectedResult);

        //act действие
        SendOtpEvent actualResult = registrationProcessor.register(request, userMapper::toUser);
        log.info("result {}", actualResult);

        //assert проверка
        Assertions.assertNotNull(actualResult);
        Assertions.assertEquals(expectedResult.email(), actualResult.email());

        verify(userMapper).toUser(eq(request), any(RoleService.class), any(BCryptPasswordEncoder.class));
        verify(userService).save(eq(userMock));
        verify(verifiedCodeService).createExpirationDate(200);
        verify(verifiedCodeService).createVerifiedCode(anyString(), eq(userMock), any(), eq(EMAIL));
        verify(verifiedCodeService).save(argThat(code ->
                code.getUser().equals(userMock)
                        && code.getCodeType().equals(EMAIL)
        ));
        verify(userMapper).toSendOtpEvent(eq(request), anyString());
    }

    @Test
    void successRegisterToAdmin() {
        //arrange подготовка
        User adminMock = User.builder()
                .name(request.name())
                .surname(request.surname())
                .phoneNumber(request.phone())
                .email(request.email())
                .build();

        VerifiedCode verifiedCodeMock = VerifiedCode.builder()
                .verifiedCode("1234")
                .user(adminMock)
                .createdAt(LocalDateTime.now())
                .codeType(EMAIL)
                .build();

        SendOtpEvent expectedResult = new SendOtpEvent(request.email(), "1234", "some text");

        //обучение моков
        when(userService.existsByPhoneNumber(request.phone())).thenReturn(false);
        when(userService.existsByEmail(request.email())).thenReturn(false);
        when(userMapper.toAdmin(eq(request), any(), any())).thenReturn(adminMock);
        when(verifiedCodeService.createVerifiedCode(anyString(), eq(adminMock), any(), eq(EMAIL)))
                .thenReturn(verifiedCodeMock);
        when(userMapper.toSendOtpEvent(eq(request), anyString())).thenReturn(expectedResult);

        //act действие
        SendOtpEvent actualResult = registrationProcessor.register(request, userMapper::toAdmin);
        log.info("result {}", actualResult);

        //assert проверка
        Assertions.assertNotNull(actualResult);
        Assertions.assertEquals(expectedResult.email(), actualResult.email());

        verify(userMapper).toAdmin(eq(request), any(RoleService.class), any(BCryptPasswordEncoder.class));
        verify(userService).save(eq(adminMock));
        verify(verifiedCodeService).createExpirationDate(200);
        verify(verifiedCodeService).createVerifiedCode(anyString(), eq(adminMock), any(), eq(EMAIL));
        verify(verifiedCodeService).save(argThat(code ->
                code.getUser().equals(adminMock)
                        && code.getCodeType().equals(EMAIL)
        ));
        verify(userMapper).toSendOtpEvent(eq(request), anyString());
    }

    @Test
    void failRegister_UserExistWithProvidedEmail() {
        //1. Arrange (подготовка)
        when(userService.existsByEmail(request.email())).thenReturn(true);

        //2. Act & assert (действие и проверка)
        assertThrows(
                EntityAlreadyExistsException.class,
                () -> registrationProcessor.register(request, userMapper::toUser)
        );

        // 3. Assert (проверка)
        verify(userService, never()).save(any());
        verifyNoInteractions(userMapper, verifiedCodeService);
    }

    @Test
    void failRegister_UserExistWithProvidedPhone() {
        //1. Arrange (подготовка)
        when(userService.existsByPhoneNumber(request.phone())).thenReturn(true);
        //2. Act & assert (действие и проверка)
        Assertions.assertThrows(EntityAlreadyExistsException.class,
                () -> registrationProcessor.register(request, userMapper::toUser)
                );
        //3. Assert (проверка)
        verify(userService, never()).save(any());
        verifyNoInteractions(userMapper, verifiedCodeService);
    }

    @Test
    void failureRegister_UserExistWithProvidedEmailButNotWithPhone(){
        //1. Arrange (подготовка)
        when(userService.existsByPhoneNumber(request.phone())).thenReturn(false);
        when(userService.existsByEmail(request.email())).thenReturn(true);
        //2. Act & assert (действие и проверка)
        Assertions.assertThrows(EntityAlreadyExistsException.class,
                () -> registrationProcessor.register(request, userMapper::toUser)
        );
        //3. Assert (проверка)
        verify(userService).existsByEmail(eq(request.email()));
        verify(userService, never()).save(any());
        verifyNoInteractions(userMapper, verifiedCodeService);
    }
}