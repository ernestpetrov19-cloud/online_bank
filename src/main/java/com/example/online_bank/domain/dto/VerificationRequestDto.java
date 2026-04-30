package com.example.online_bank.domain.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import org.hibernate.validator.constraints.UUID;

import static org.hibernate.validator.constraints.UUID.LetterCase.INSENSITIVE;

/**
 * Этот объект представляет собой запрос на верификацию.
 *
 * @param email            Электронная почта
 * @param verificationCode Код подтверждения
 * @param deviceName       имя устройства
 * @param userAgent        средство откуда использовано
 */
public record VerificationRequestDto(
        @Schema(description = "Почта", example = "example@gmail.com")
        @NotBlank(message = "Email не может быть пустым")
        String email,
        @Schema(description = "Код", example = "1753")
        @NotBlank(message = "Код не может быть пустым")
        String verificationCode,
        @NotBlank
        String deviceName,
        @NotBlank
        String userAgent,
        @NotBlank
        @UUID(letterCase = INSENSITIVE)
        @Size(max = 512)
        String deviceId
) {
}
