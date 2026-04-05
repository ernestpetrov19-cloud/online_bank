package com.example.online_bank.domain.entity;

import com.example.online_bank.enums.CurrencyCode;
import jakarta.persistence.*;
import lombok.*;

import java.math.BigDecimal;

import static jakarta.persistence.EnumType.STRING;
import static jakarta.persistence.GenerationType.IDENTITY;

/**
 * Base - батька(пишется слева)
 * Target - сынку(пишется справа)
 * ExchangeRate - Курс обмена.
 * Пример записи - доллар/рубль 90. Это значит, что один доллар стоит 90 рублей
 */
@Builder
@Getter
@Setter
@ToString
@AllArgsConstructor
@NoArgsConstructor
@Entity
public class ExchangeRate {
    @Id
    @GeneratedValue(strategy = IDENTITY)
    private Long id;
    @Column
    @Enumerated(STRING)
    private CurrencyCode baseCurrency;     // Валюта, к которой происходит обмен(USD)
    @Column
    @Enumerated(STRING)
    private CurrencyCode targetCurrency;   // Валюта, от которой происходит обмен(RUB)
    @Column(precision = 9, scale = 5)
    private BigDecimal rate; // цена котируемой валюты по отношению к базовой
}
