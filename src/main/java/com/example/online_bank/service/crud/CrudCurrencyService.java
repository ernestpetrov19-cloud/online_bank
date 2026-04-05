package com.example.online_bank.service.crud;

import com.example.online_bank.domain.dto.RateRequestDto;
import com.example.online_bank.domain.dto.RateResponseDto;
import com.example.online_bank.domain.entity.ExchangeRate;
import com.example.online_bank.enums.CurrencyCode;
import com.example.online_bank.repository.ExchangeCurrencyRepository;
import com.example.online_bank.service.CurrencyRateProvider;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;

@Service
@RequiredArgsConstructor
@Slf4j
public class CrudCurrencyService {
    private final ExchangeCurrencyRepository currencyRepository;
    private final CurrencyRateProvider currencyRateProvider;

    /**
     * Создает запись курса
     *
     * @param baseCurrency   Валюта, от которой происходит обмен(USD)
     * @param targetCurrency Валюта, к которой происходит обмен(RUB)
     * @param rate           Цена котируемой валюты по отношению к базовой.
     *                       //Пример: доллар рубль 90. Т.е за 1 доллар получим 90 рублей.
     */
    @Transactional()
    public RateResponseDto create(
            CurrencyCode baseCurrency,
            CurrencyCode targetCurrency,
            BigDecimal rate) {

        ExchangeRate entity = ExchangeRate.builder()
                .baseCurrency(baseCurrency)
                .targetCurrency(targetCurrency)
                .rate(rate)
                .build();

        currencyRepository.save(entity);
        return new RateResponseDto(baseCurrency, targetCurrency, rate);
    }

    /**
     * Найти курс
     *
     * @return Курс базовой валюты по отношению к котируемой валюте
     * Если оказывается что есть только перевернутый курс, то считаем его по формуле 1 / на курс.
     * Например:
     * У нас есть курс "доллар - рубль = 90". В метод "найти курс" передали - "рубль, доллар".
     * У нас нет валютной пары "рубль - доллар", но есть "доллар - рубль".
     * Соответственно, мы вернем курс 1 / 90 = 0,01111.
     */
    @Transactional
    public RateResponseDto findRate(RateRequestDto rateRequestDto) {
        BigDecimal rate = currencyRateProvider.findRate(rateRequestDto.baseCurrency(), rateRequestDto.targetCurrency());
        return new RateResponseDto(
                rateRequestDto.baseCurrency(),
                rateRequestDto.targetCurrency(),
                rate
        );
    }

}