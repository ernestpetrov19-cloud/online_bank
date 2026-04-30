package com.example.online_bank.service;

import com.example.online_bank.domain.entity.User;
import com.example.online_bank.repository.UserRepository;
import com.example.online_bank.security.userdetails.CustomUserDetails;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.core.userdetails.UsernameNotFoundException;

import java.util.Optional;

import static com.example.online_bank.enums.TestUserData.EMAIL;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.when;

@Slf4j
@ExtendWith(MockitoExtension.class)
class UserServiceTest {
    @Mock
    private UserRepository userRepository;
    @InjectMocks
    private UserService userService;

    @Test
    @Disabled
    void successLoadUserByUsername() {
        User user = User.builder().id(1L).name("Test").build();
        when(userRepository.findByName("Test")).thenReturn(Optional.ofNullable(user));
        CustomUserDetails userDetails = (CustomUserDetails) userService.loadUserByUsername("Test");
        assertNotNull(userDetails);
        assertEquals("Test", userDetails.getUsername());
    }

    @Test
    void failLoadUserByUsername() {
        when(userRepository.findByEmail(EMAIL.getValue())).thenReturn(Optional.empty());
        assertThrows(UsernameNotFoundException.class, () -> userService.loadUserByUsername(EMAIL.getValue()));
    }
}