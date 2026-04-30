package com.example.online_bank.security.jwt.factory;

import com.example.online_bank.domain.dto.UserContainer;
import com.example.online_bank.enums.TokenType;

public interface TokenProvider {
    /**
     * @param userContainer - Информация о пользователе.
     * @return Refresh токен.
     */
    String create(TokenType type, UserContainer userContainer);

    /**
     * @param supported - Поддерживаемый тип токена
     * @return - True, если токен поддерживается и false, если нет.
     */
    boolean supports(TokenType supported);
}
