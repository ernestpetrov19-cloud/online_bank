package com.example.online_bank.domain.dto;

import com.example.online_bank.enums.CurrencyCode;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;

import java.math.BigDecimal;

public record ConvertCurrencyRequestDto(
        @NotNull
        CurrencyCode baseCurrency,
        @NotNull
        CurrencyCode targetCurrency,
        @Positive(message = "Неверно переданное количество к переводу")
        BigDecimal providedAmountInBaseCurrency
) {
}
