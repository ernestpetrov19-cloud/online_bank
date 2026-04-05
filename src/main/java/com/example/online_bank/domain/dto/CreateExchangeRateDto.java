package com.example.online_bank.domain.dto;

import com.example.online_bank.enums.CurrencyCode;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;

import java.math.BigDecimal;

/**
 * Создать курс. Например, доллар/рубль 90, то есть за один доллар можно получить 90 рублей
 *
 * @param baseCurrency   Базовая валюта
 * @param targetCurrency Котируемая валюта
 * @param rate           Количество котируемой валюты по отношению к базовой
 */
public record CreateExchangeRateDto(
        @NotNull
        CurrencyCode baseCurrency,
        @NotNull
        CurrencyCode targetCurrency,
        @Positive(message = "Цена котируемой валюты должна быть больше нуля")
        @DecimalMin(value = "1", message = "Цена котируемой валюты должна быть больше 0", inclusive = false)
        BigDecimal rate
) {
}
