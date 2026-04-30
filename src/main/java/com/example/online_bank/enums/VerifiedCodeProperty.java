package com.example.online_bank.enums;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum VerifiedCodeProperty {
    EXP_DATE(120);
    private final int seconds;
}
