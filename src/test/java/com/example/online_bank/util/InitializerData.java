package com.example.online_bank.util;

import com.example.online_bank.domain.dto.UserContainer;
import com.example.online_bank.service.TokenService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Component;

import java.util.List;

import static java.util.UUID.randomUUID;

@Component
public class InitializerData<T, R extends JpaRepository<T, Long>> {
    @Autowired
    private TokenService tokenService;

    public String initUser(R repository) {
        repository.deleteAll();
        UserContainer userContainer = new UserContainer(
                randomUUID().toString(),
                "testUser",
                List.of("ROLE_USER"));
        return tokenService.getAccessToken(userContainer);
    }

    public String initAdmin(R repository) {
        repository.deleteAll();
        repository.deleteAll();
        UserContainer userContainer = new UserContainer(
                randomUUID().toString(),
                "testAdmin",
                List.of("ROLE_ADMIN"));
        return tokenService.getAccessToken(userContainer);
    }

}
