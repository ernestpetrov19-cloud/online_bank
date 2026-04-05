package com.example.online_bank.service;

import com.example.online_bank.enums.CurrencyCode;
import com.example.online_bank.exception.InvertedRateNotFoundException;
import com.example.online_bank.repository.ExchangeCurrencyRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;

import static java.math.BigDecimal.ONE;
import static java.math.RoundingMode.HALF_EVEN;

@Slf4j
@RequiredArgsConstructor
@Service
public class LocalDataBaseCurrencyRateProvider implements CurrencyRateProvider {
    private final ExchangeCurrencyRepository currencyRepository;

    @Override
    public BigDecimal findRate(CurrencyCode baseCurrency, CurrencyCode targetCurrency) throws InvertedRateNotFoundException {
        BigDecimal rate = currencyRepository.findRateByBaseAndTargetCurrency(baseCurrency, targetCurrency)
                .orElseGet(() -> calcInvertedRate(baseCurrency, targetCurrency));
        log.info("Founded rate is - {}", rate);
        return rate;
    }

    /**
     * Находим ставку перевернутого курса, делаем расчет курса для изначальной пары валют
     */
    private BigDecimal calcInvertedRate(CurrencyCode baseCurrency, CurrencyCode targetCurrency) {
        log.trace("Searching by inverted rate..");
        BigDecimal invertedRate = currencyRepository.findRateByBaseAndTargetCurrency(targetCurrency, baseCurrency)
                .orElseThrow(() -> new InvertedRateNotFoundException("Перевернутый курс не найден"));
        log.info("Founded inverted rate is - {}", invertedRate);
        return ONE.divide(invertedRate, 5, HALF_EVEN);
    }
}
