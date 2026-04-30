package com.example.online_bank.security.jwt.factory;

import com.example.online_bank.domain.dto.UserContainer;
import com.example.online_bank.enums.TokenType;

import java.time.LocalDateTime;

//todo not finished
public class AbstractTokenProvider implements TokenProvider {

    @Override
    public String create(TokenType type, UserContainer userContainer) {
        LocalDateTime issuedDate = LocalDateTime.now();
        LocalDateTime notBeforeDate = LocalDateTime.now();
        return null;
    }

    @Override
    public boolean supports(TokenType supported) {
        return false;
    }
}
