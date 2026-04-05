package com.example.online_bank.controller;

import com.example.online_bank.service.TokenService;
import jakarta.transaction.Transactional;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.DEFINED_PORT)
@AutoConfigureMockMvc
@Transactional
class AccountControllerTestIt {
    private static final String BASE_URL = "/api/account";
    private String token;
    @Autowired
    private TokenService tokenService;

    @Test
    void successCreateAccount() {

    }

    @Test
    void getBalance() {
    }

    @Test
    void findAllByHolder() {
    }
}