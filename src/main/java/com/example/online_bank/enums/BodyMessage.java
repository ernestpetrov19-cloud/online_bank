package com.example.online_bank.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum BodyMessage {
    VERIFICATION_BODY("Ваш код подтверждения регистрации: "),
    CONFIRM_LOGIN("Подтвердите вход с нового устройства, введя проверочный код: ");
    private final String value;
}
