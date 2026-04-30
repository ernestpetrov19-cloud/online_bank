package com.example.online_bank.domain.dto;

import com.example.online_bank.domain.entity.User;

public record VerificationResponseDto(
        User verifiedUser
) {
}
