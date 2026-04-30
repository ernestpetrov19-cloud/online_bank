package com.example.online_bank.domain.dto;

import io.swagger.v3.oas.annotations.media.Schema;

public record SenderInfo(
        @Schema(description = "Номер счета отправителя", example = "810000001")
        String accountNumberFrom
) {
}
