package com.example.online_bank.security.jwt.factory;

import com.example.online_bank.domain.dto.UserContainer;
import com.example.online_bank.enums.TokenType;

/**
 * Этот класс выдает необходимый тип токена
 */
public interface TokenResolver {
    /**
     * Создает jwt токен на основе переданного типа.
     * У каждого провайдера вызывается метод supports() для того,
     * чтобы узнать, поддерживается ли переданный тип токена или нет.
     */
    String createJwt(TokenType tokenType, UserContainer userContainer);
}
