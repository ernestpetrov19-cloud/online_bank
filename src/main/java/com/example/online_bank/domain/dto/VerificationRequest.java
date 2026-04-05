package com.example.online_bank.domain.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;

/**
 * Этот объект представляет собой запрос на аутентификацию. Используется в аутентификации по электронной почте
 *
 * @param email      Электронная почта
 * @param code       Код подтверждения
 * @param deviceName имя устройства
 * @param userAgent  средство откуда использовано
 */
public record VerificationRequest(
        @Schema(description = "Почта", example = "example@gmail.com")
        @NotBlank(message = "Email не может быть пустым")
        String email,
        @Schema(description = "Пин-код", example = "1753")
        @NotBlank(message = "Код не может быть пустым")
        String code,
        String deviceName,
        String userAgent,
        String deviceId
) {
}
