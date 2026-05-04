package com.example.online_bank.domain.event;

public record SendVerificationCodeEvent(
        String userEmail,
        String code,
        String subjectMessage,
        String bodyMessage
) {
}
