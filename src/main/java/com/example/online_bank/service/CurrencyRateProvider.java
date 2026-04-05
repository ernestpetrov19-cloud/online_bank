package com.example.online_bank.service;

import com.example.online_bank.enums.CurrencyCode;

import java.math.BigDecimal;

public interface CurrencyRateProvider {
    BigDecimal findRate(CurrencyCode baseCurrency, CurrencyCode targetCurrency);
}
