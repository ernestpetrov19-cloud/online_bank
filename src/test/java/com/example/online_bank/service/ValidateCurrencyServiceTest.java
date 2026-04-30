package com.example.online_bank.service;

import com.example.online_bank.domain.dto.ConvertCurrencyResponseDto;
import com.example.online_bank.enums.CurrencyCode;
import com.example.online_bank.repository.ExchangeCurrencyRepository;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;

import static java.math.BigDecimal.TEN;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
@Slf4j
class ValidateCurrencyServiceTest {
    @InjectMocks
    ValidateCurrencyService validateCurrencyService;

    @Mock
    CurrencyConversionService currencyConversionService;

    @Mock
    ExchangeCurrencyRepository exchangeCurrencyRepository;

    @Mock
    AccountService accountService;

    @Test
    void successProcessTransactionAndCurrencyCodesIsEquals() {
        CurrencyCode accountCurrencyCode = CurrencyCode.USD;
        CurrencyCode selectedCurrencyCode = CurrencyCode.USD;

        BigDecimal result = validateCurrencyService.processTransaction(accountCurrencyCode, selectedCurrencyCode, accountService::withdrawMoney, "001", TEN);
        log.info("{}", result);
        Assertions.assertEquals(TEN, result);
    }

    @Test
    void successProcessTransactionCurrencyCodeIsDifferent() {
        CurrencyCode accountCurrencyCode = CurrencyCode.RUB;
        CurrencyCode selectedCurrencyCode = CurrencyCode.CNY;
        String accountNumberTo = "0002";
        BigDecimal amount = BigDecimal.valueOf(200);

        when(currencyConversionService.convert(accountCurrencyCode, selectedCurrencyCode, amount))
                .thenReturn(new ConvertCurrencyResponseDto(accountCurrencyCode, selectedCurrencyCode, BigDecimal.valueOf(2000), amount));
        when(exchangeCurrencyRepository.existsByBaseCurrencyAndTargetCurrency(selectedCurrencyCode, accountCurrencyCode)).thenReturn(true);
        BigDecimal result = validateCurrencyService.processTransaction(accountCurrencyCode, selectedCurrencyCode, accountService::withdrawMoney, "001", amount);
        Assertions.assertEquals(BigDecimal.valueOf(2000), result);
    }
}