package com.example.online_bank.service;

import com.example.online_bank.exception.InvertedRateNotFoundException;
import com.example.online_bank.repository.ExchangeCurrencyRepository;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.util.Optional;

import static com.example.online_bank.enums.CurrencyCode.RUB;
import static com.example.online_bank.enums.CurrencyCode.USD;
import static java.math.BigDecimal.valueOf;
import static java.util.Optional.of;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.Mockito.when;


@ExtendWith(MockitoExtension.class)
@Slf4j
class LocalDataBaseCurrencyRateProviderTest {
    @Mock
    private ExchangeCurrencyRepository exchangeCurrencyRepository;
    @InjectMocks
    private LocalDataBaseCurrencyRateProvider localDataBaseCurrencyRateProvider;

    @Test
    void successCalcRate() {
        //для случая когда курс доллар рубль 90 не найден
        when(exchangeCurrencyRepository.findRateByBaseAndTargetCurrency(USD, RUB)).thenReturn(of(valueOf(90)));
        BigDecimal result = localDataBaseCurrencyRateProvider.findRate(USD, RUB);

        assertNotNull(result);
        assertEquals(0, result.compareTo(valueOf(90)));
    }

    @Test
    void successFindRateByInverted() {
        when(exchangeCurrencyRepository.findRateByBaseAndTargetCurrency(USD, RUB)).thenReturn(Optional.empty());
        when(exchangeCurrencyRepository.findRateByBaseAndTargetCurrency(RUB, USD)).thenReturn(of(valueOf(0.01111)));
        BigDecimal result = localDataBaseCurrencyRateProvider.findRate(USD, RUB);
        log.info("result, {}", result);
        assertNotNull(result);
        assertEquals(0, result.compareTo(valueOf(90.00900)));
    }


    @Test
    void failureFindRate() {
        when(exchangeCurrencyRepository.findRateByBaseAndTargetCurrency(USD, RUB)).thenReturn(Optional.empty());
        when(exchangeCurrencyRepository.findRateByBaseAndTargetCurrency(RUB, USD)).thenReturn(Optional.empty());

        Assertions.assertThrows(InvertedRateNotFoundException.class, () -> localDataBaseCurrencyRateProvider.findRate(USD, RUB));
    }

}