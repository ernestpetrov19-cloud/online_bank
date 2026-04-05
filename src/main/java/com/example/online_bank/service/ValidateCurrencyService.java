package com.example.online_bank.service;

import com.example.online_bank.domain.dto.ConvertCurrencyResponseDto;
import com.example.online_bank.enums.CurrencyCode;
import com.example.online_bank.exception.CurrencyPairsNotFoundException;
import com.example.online_bank.repository.ExchangeCurrencyRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.function.BiConsumer;

@Service
@RequiredArgsConstructor
public class ValidateCurrencyService {
    private final CurrencyConversionService currencyConversionService;
    private final ExchangeCurrencyRepository exchangeCurrencyRepository;

    /**
     * Сверяет код валюты для снятия с кодом валюты кошелька.
     * Если коды разнятся, то производит конвертацию, возвращает итоговую сумму
     */
    @Transactional
    public BigDecimal processTransaction(
            CurrencyCode accountCurrencyCode,
            CurrencyCode selectedCurrencyCode,
            BiConsumer<String, BigDecimal> operationMethodReference,
            String accountNumberTo,
            BigDecimal amount
    ) {
        BigDecimal finalAmount;
        if (accountCurrencyCode.equals(selectedCurrencyCode)) {
            finalAmount = amount;
            operationMethodReference.accept(accountNumberTo, finalAmount); //accountService.someOneOperationType(*accountNumber, providedAmountInBaseCurrency*)
        } else {
            if (!exchangeCurrencyRepository.existsByBaseCurrencyAndTargetCurrency(selectedCurrencyCode, accountCurrencyCode)) {
                throw new CurrencyPairsNotFoundException("Курс не существует. Выберите другую валюту для пополнения.");
            }

            ConvertCurrencyResponseDto convertCurrencyResponseDto = currencyConversionService.convert(
                    accountCurrencyCode,
                    selectedCurrencyCode,
                    amount
            );
            finalAmount = convertCurrencyResponseDto.targetConvertedAmount();
            operationMethodReference.accept(accountNumberTo, finalAmount);
        }
        return finalAmount;
    }
}