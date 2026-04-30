package com.example.online_bank.service.impl;

import com.example.online_bank.service.NotificationService;
import com.resend.Resend;
import com.resend.core.exception.ResendException;
import com.resend.services.emails.model.CreateEmailOptions;
import com.resend.services.emails.model.CreateEmailResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Primary;
import org.springframework.stereotype.Service;

@Slf4j
@Service("resendService")
@Primary
public class ResendNotificationService implements NotificationService {
    public static final String SENDER = "onboarding@resend.dev";
    @Value("${resend.api-key}")
    private String apikey;

    @Override
    public void sendVerificationCode(String to, String subject, String body) {
        Resend resend = new Resend(apikey);

        CreateEmailOptions sendEmailRequest = CreateEmailOptions.builder()
                .from(SENDER)
                .to(to)
                .subject(subject)
                .html(body)
                .build();

        try {
            CreateEmailResponse data = resend.emails().send(sendEmailRequest);
        } catch (ResendException e) {
            log.error(e.getMessage());
            throw new RuntimeException(e);
        }
    }
}
