package com.example.online_bank.domain.dto;

import java.math.BigDecimal;

public record PayDtoRequest(
        SenderInfo senderInfo,
        ServiceInfo serviceInfo,
        BigDecimal serviceRequestAmount
) {
}
