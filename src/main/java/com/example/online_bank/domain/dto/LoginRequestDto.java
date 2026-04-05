package com.example.online_bank.domain.dto;

public record LoginRequestDto(
        String email,
        String password,
        String deviceId,
        String deviceName,
        String userAgent
) {
}
