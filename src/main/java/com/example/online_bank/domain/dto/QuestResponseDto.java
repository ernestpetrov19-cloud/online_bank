package com.example.online_bank.domain.dto;

import com.example.online_bank.enums.PartnerCategory;

import java.time.LocalDate;

public record QuestResponseDto(
        PartnerCategory category,
        LocalDate dateOfExpiry,
        Integer pointReward,
        Integer progress
) {
}
