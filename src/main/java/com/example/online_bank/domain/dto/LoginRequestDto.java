package com.example.online_bank.domain.dto;

import jakarta.validation.constraints.NotBlank;

public record LoginRequestDto(
        @NotBlank(message = "Email must be not blank")
        String email,
        @NotBlank(message = "Password must be not blank")
        String password,
        @NotBlank(message = "Device id must be not blank")
        String deviceId,
        @NotBlank(message = "Device name must be not blank")
        String deviceName,
        @NotBlank(message = "UserAgent must be not blank")
        String userAgent
) {
}
