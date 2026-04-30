package com.example.online_bank.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum AuthenticationResponseKey {
    ACCESS_TOKEN("accessToken"),
    ID_TOKEN("idToken"),
    REFRESH_TOKEN("refreshToken");

    private final String value;
}
