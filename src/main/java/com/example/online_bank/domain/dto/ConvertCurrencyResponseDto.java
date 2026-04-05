package com.example.online_bank.domain.dto;

import com.example.online_bank.enums.CurrencyCode;

import java.math.BigDecimal;

public record ConvertCurrencyResponseDto(
        CurrencyCode baseCurrency,
        CurrencyCode targetCurrency,
        BigDecimal targetConvertedAmount,
        BigDecimal providedAmountInBaseCurrency
) {
}
