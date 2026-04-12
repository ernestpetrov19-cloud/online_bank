package com.example.online_bank.domain.dto;

import io.swagger.v3.oas.annotations.media.Schema;

/**
 * DTO для регистрации
 *
 * @param phone      Номер телефона
 * @param name       Имя
 * @param surname    Фамилия
 * @param patronymic Отчество
 */
public record RegistrationDtoRequest(
        @Schema(description = "Имя пользователя", example = "Амир")
        String name,
        @Schema(description = "Фамилия пользователя", example = "Гильманов")
        String surname,
        @Schema(description = "Отчество пользователя", example = "Азатович")
        String patronymic,
        @Schema(description = "Номер телефона", example = "+79992281488")
        String phone,
        @Schema(description = "Пароль", example = "mypass1234")
        String password,
        @Schema(description = "Электронная почта", example = "myemail@.com")
        String email
) {
}
