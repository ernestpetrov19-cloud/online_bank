package com.example.online_bank.domain.dto;

import com.example.online_bank.enums.PartnerCategory;

import java.time.LocalDate;

public record UserQuestResponseDto(
        String name,
        PartnerCategory questCategory,
        LocalDate questExpireDate,
        Integer pointReward,
        Integer necessaryToReward,
        Integer userProgress,
        Boolean isComplete
) {
}
