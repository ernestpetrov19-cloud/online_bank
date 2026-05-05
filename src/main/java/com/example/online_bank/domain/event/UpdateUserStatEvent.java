package com.example.online_bank.domain.event;

import com.example.online_bank.enums.PartnerCategory;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.UUID;

public record UpdateUserStatEvent(
        UUID userUuid,
        BigDecimal spendAmount,
        LocalDate operationDate,
        String userAccount,
        PartnerCategory partnerCategory
) {
}
