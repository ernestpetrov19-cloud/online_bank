package com.example.online_bank.domain.dto;

import com.example.online_bank.enums.PartnerCategory;

import java.math.BigDecimal;
import java.time.LocalDate;

/**
 * @param name Имя
 * @param questCategory Категория квеста
 * @param questExpireDate  Дата истечения
 * @param necessaryToReward Награда за квест
 * @param userProgress Прогресс пользователя
 * @param isComplete Завершен?
 * @param progressInPercent Прогресс в процентах
 */
public record UserQuestResponseDto(
        String name,
        PartnerCategory questCategory,
        LocalDate questExpireDate,
        BigDecimal necessaryToReward,
        BigDecimal userProgress,
        Boolean isComplete,
        BigDecimal progressInPercent
) {
}
