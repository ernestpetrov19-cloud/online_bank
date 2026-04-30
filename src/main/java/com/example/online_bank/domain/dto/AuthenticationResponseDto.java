package com.example.online_bank.domain.dto;

import java.util.Map;

/**
 * @param tokens Токены для работы приложения
 */
public record AuthenticationResponseDto(Map<String, String> tokens) {
}
