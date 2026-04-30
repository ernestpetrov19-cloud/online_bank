package com.example.online_bank.service;

import com.example.online_bank.domain.dto.ConvertCurrencyResponseDto;
import com.example.online_bank.exception.InvertedRateNotFoundException;
import com.example.online_bank.service.impl.LocalDataBaseCurrencyRateProvider;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;

import static com.example.online_bank.enums.CurrencyCode.RUB;
import static com.example.online_bank.enums.CurrencyCode.USD;
import static java.math.BigDecimal.valueOf;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
@Slf4j
class CurrencyConversionServiceTest {
    @Mock
    LocalDataBaseCurrencyRateProvider localDataBaseCurrencyRateProvider;
    @InjectMocks
    private CurrencyConversionService currencyConversionService;

    @Test
    void successConvert() {
        BigDecimal amount = valueOf(100);

        when(localDataBaseCurrencyRateProvider.findRate(USD, RUB))
                .thenReturn((valueOf(90)));
        ConvertCurrencyResponseDto responseDto = currencyConversionService
                .convert(USD, RUB, amount);
        log.info("{}", responseDto);
        BigDecimal rate = responseDto.targetConvertedAmount();

        assertEquals(valueOf(9000), rate);
    }

    @Test
    void successConvert_ByInvertedRate() {
        //хотим 15$ перевести в рубли
        //курс доллар рубль 90 не найден,
        // чтобы найти мы 15 * 90 = 1350
        // есть курс RUB USD 0.01111
        // 1 / 0.01111 = 90.09
        // 15 * 90.09

        BigDecimal amount = valueOf(15);
        when(localDataBaseCurrencyRateProvider.findRate(USD, RUB)).thenReturn((valueOf(90.00900)));

        ConvertCurrencyResponseDto result = currencyConversionService.convert(USD, RUB, amount);
        log.info("{}", result);
        assertEquals(new BigDecimal("15"), result.providedAmountInBaseCurrency());
        assertEquals(new BigDecimal("1350.135"), result.targetConvertedAmount());
    }

    @Test
    void failConvert_InvertedRateNotFound() {
        BigDecimal amount = valueOf(15);
        when(localDataBaseCurrencyRateProvider.findRate(USD, RUB)).thenThrow(InvertedRateNotFoundException.class);

        assertThrows(InvertedRateNotFoundException.class, () -> currencyConversionService.convert(USD, RUB, amount));
    }
}