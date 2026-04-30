package com.example.online_bank.domain.event;

import com.example.online_bank.enums.BodyMessage;
import com.example.online_bank.enums.SubjectMessage;

public record SendVerificationCodeEvent(
        String userEmail,
        String code,
        SubjectMessage subjectMessage,
        BodyMessage bodyMessage
) {
}
