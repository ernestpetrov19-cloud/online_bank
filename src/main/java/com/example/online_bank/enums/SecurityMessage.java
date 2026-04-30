package com.example.online_bank.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum SecurityMessage {
    HACKING_ATTEMPT_DETECTED("Обнаружена попытка взлома! Рекомендуем срочно сменить пароль"),
    CONFIRM_LOGIN_MESSAGE("Подтвердите вход с нового устройства, введя проверочный код");

    private final String value;
}
