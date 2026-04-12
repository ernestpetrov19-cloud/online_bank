package com.example.online_bank.util;

import com.example.online_bank.domain.dto.UserContainer;
import com.example.online_bank.service.TokenService;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.List;

import static java.util.UUID.randomUUID;

@Component
@RequiredArgsConstructor
public class InitializerData {
    @Autowired
    private final TokenService tokenService;

    public String initUser() {
        UserContainer userContainer = new UserContainer(
                randomUUID().toString(),
                "testUser",
                List.of("ROLE_USER"));
        return tokenService.getAccessToken(userContainer);
    }

    public String initAdmin() {
        UserContainer userContainer = new UserContainer(
                randomUUID().toString(),
                "testAdmin",
                List.of("ROLE_ADMIN"));
        return tokenService.getAccessToken(userContainer);
    }

}
