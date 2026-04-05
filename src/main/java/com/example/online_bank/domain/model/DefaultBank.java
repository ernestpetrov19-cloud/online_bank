package com.example.online_bank.domain.model;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.stereotype.Component;

@Component
@ConditionalOnMissingBean(value = {EuroBank.class, MoneyBank.class})
public class DefaultBank extends AbstractBank {
    public DefaultBank(
            @Value("${default-bank.bank.name}") String name,
            @Value("${default-bank.bank.partner.prefix-url}") String prefixUrl) {
        super(name, prefixUrl);
    }
}
