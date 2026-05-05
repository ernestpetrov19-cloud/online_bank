package com.example.online_bank.domain.event;

import com.example.online_bank.domain.entity.User;
import com.example.online_bank.enums.PartnerCategory;

import java.math.BigDecimal;
import java.time.LocalDate;

/**
 * @param category Категория
 * @param user Пользователь
 * @param lastSpendDate Последняя дата платежа
 * @param userAccountNumber Номер счета пользователя
 * @param totalSpendAmount Всего потрачено
 */
public record UpdateUserQuestEvent(
        PartnerCategory category,
        User user,
        LocalDate lastSpendDate,
        String userAccountNumber,
        BigDecimal totalSpendAmount
) {
}
