package com.example.online_bank.domain.dto;

public record LogoutRequestDto(
        String token,
        String deviceId
) {
}
