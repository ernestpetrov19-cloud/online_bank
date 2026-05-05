package com.example.online_bank.domain.event;

import java.math.BigDecimal;

public record UpdateBonusAccountEvent(
        BigDecimal points,
        String accountNumber
) {
}
