package com.example.online_bank.service;

import com.example.online_bank.domain.dto.RegistrationDto;
import com.example.online_bank.exception.EntityAlreadyExistsException;
import com.example.online_bank.mapper.UserMapper;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.test.context.junit.jupiter.SpringExtension;

@ExtendWith(SpringExtension.class)
class RegistrationServiceTest {
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
    @InjectMocks
    private RegistrationService registrationService;

    @Test
    @Disabled
    void successSignUp() {
        //arrange Подготовка данных
        RegistrationDto registrationDto = new RegistrationDto(
                "testName",
                "testSurname",
                "testPatronymic",
                "89608052696",
                "wass",
                "myemail@.com"
        );
        Assertions.assertDoesNotThrow(() -> registrationService.signUp(registrationDto));
    }

    @Test
    @Disabled
    void failedSignUpByPhoneNumberAlreadyExists() {
        //arrange Подготовка данных
        RegistrationDto registrationDto = new RegistrationDto(
                "testName",
                "testSurname",
                "testPatronymic",
                "89608052696",
                "wass",
                "myemail@.com"
        );
        Mockito.when(userService.existsByPhoneNumber("89608052696")).thenReturn(true);

        Assertions.assertThrows(EntityAlreadyExistsException.class, () -> registrationService.signUp(registrationDto));
    }

    @Test
    @Disabled
    void failedSignUpByEmailAlreadyExists() {
        //arrange Подготовка данных
        RegistrationDto registrationDto = new RegistrationDto(
                "testName",
                "testSurname",
                "testPatronymic",
                "89608052696",
                "wass",
                "myemail@.com"
        );
        Mockito.when(userService.existsByEmail("myemail@.com")).thenReturn(true);
        Assertions.assertThrows(EntityAlreadyExistsException.class, () -> registrationService.signUp(registrationDto));
    }
}