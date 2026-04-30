package com.example.online_bank.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum TestUserData {
    NAME("John"),
    SURNAME("Doe"),
    PATRONYMIC(""),
    EMAIL("testemail@gmail.com"),
    PHONE_NUMBER("79999999999"),
    VERIFICATION_CODE("1234"),
    DEVICE_NAME("Windows PC"),
    CHROME_USER_AGENT("Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/147.0.0.0 Safari/537.36"),
    UPDATED_CHROME_USER_AGENT("Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/148.0.0.0 Safari/537.36"),
    OPERA_USER_AGENT("Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/146.0.0.0 Safari/537.36 OPR/130.0.0.0 (Edition Yx 08)"),
    EDG_USER_AGENT("Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/146.0.0.0 Safari/537.36 Edg/146.0.0.0"),
    PASSWORD("1234");
    private final String value;
}
