package com.example.online_bank.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum SubjectMessage {
    VERIFICATION("Подтверждение регистрации"),
    AUTHENTICATION("Подтверждение входа"),
    RESEND("Повторная отправка кода");
    private final String value;
}
