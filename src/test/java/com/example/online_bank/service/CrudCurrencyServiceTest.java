package com.example.online_bank.service;

import com.example.online_bank.domain.dto.RateResponseDto;
import com.example.online_bank.domain.entity.ExchangeRate;
import com.example.online_bank.repository.ExchangeCurrencyRepository;
import com.example.online_bank.service.crud.CrudCurrencyService;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static com.example.online_bank.enums.CurrencyCode.RUB;
import static com.example.online_bank.enums.CurrencyCode.USD;
import static java.math.BigDecimal.valueOf;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
@Slf4j
class CrudCurrencyServiceTest {
    @Mock
    private ExchangeCurrencyRepository exchangeCurrencyRepository;
    @InjectMocks
    private CrudCurrencyService crudCurrencyService;

    @Test
    void successCreateRate() {
        ExchangeRate savedEntity = ExchangeRate.builder()
                .baseCurrency(USD)
                .targetCurrency(RUB)
                .rate(valueOf(90))
                .id(1L)
                .build();

        when(exchangeCurrencyRepository.save(any(ExchangeRate.class))).thenReturn(savedEntity);
        RateResponseDto rateResponseDto = crudCurrencyService.create(USD, RUB, valueOf(90));
        log.info("{}", rateResponseDto);
        assertEquals(valueOf(90), rateResponseDto.rate());
    }
}