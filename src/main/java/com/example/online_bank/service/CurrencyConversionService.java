package com.example.online_bank.service;

import com.example.online_bank.domain.dto.ConvertCurrencyResponseDto;
import com.example.online_bank.enums.CurrencyCode;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;

@Service
@RequiredArgsConstructor
public class CurrencyConversionService {
    private final CurrencyRateProvider currencyRateProvider;

    /**
     * Произвести конвертацию. Сумма не может быть нулевой или отрицательной
     * <p>
     * 1) сравниваем полученные данные
     * 2) находим ставку по курсам
     * 3) ставку умножаем на переданное количество
     *
     * @param baseCurrency                 Во что конвертировать
     * @param targetCurrency               Что конвертировать
     * @param providedAmountInBaseCurrency Сколько конвертировать (в валюте base)
     * @return Сумма в таргет
     * <p>
     * @example base - USD
     * target - RUB
     * providedAmountInBaseCurrency - 9 USD
     * нашли ставку - 90rub
     * 9usd * 90rub
     * получаем: 8100rub
     */
    public ConvertCurrencyResponseDto convert(
            CurrencyCode baseCurrency, // во что конвертировать
            CurrencyCode targetCurrency, //что конвертировать
            BigDecimal providedAmountInBaseCurrency //сколько конвертировать
    ) {
        //1) находим ставку
        BigDecimal foundedRate = currencyRateProvider.findRate(baseCurrency, targetCurrency);

        //2) сколько получим базовой (base)
        BigDecimal targetConvertedAmount = providedAmountInBaseCurrency.multiply(foundedRate);

        //3) собираем ответ
        return new ConvertCurrencyResponseDto(
                baseCurrency,
                targetCurrency,
                targetConvertedAmount,
                providedAmountInBaseCurrency
        );
    }
}
