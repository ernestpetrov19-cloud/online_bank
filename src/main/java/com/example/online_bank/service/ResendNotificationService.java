package com.example.online_bank.service;

import com.resend.Resend;
import com.resend.core.exception.ResendException;
import com.resend.services.emails.model.CreateEmailOptions;
import com.resend.services.emails.model.CreateEmailResponse;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Primary;
import org.springframework.stereotype.Service;

@Service("resendService")
@Primary
public class ResendNotificationService implements NotificationService {
    public static final String SENDER = "onboarding@resend.dev";
    @Value("${resend.api-key}")
    private String apikey;
    private final static String EMAIL_SUBJECT = "Код подтверждения";
    private final static String BODY_TEXT = "Ваш код подтверждения регистрации: ";

    /**
     * @param to
     * @param verificationCode
     * @param bodyText
     */
    @Override
    public void sendOtpCode(String to, String verificationCode, String bodyText) {
        Resend resend = new Resend(apikey);

        CreateEmailOptions sendEmailRequest = CreateEmailOptions.builder()
                .from(SENDER)
                .to(to)
                .subject(EMAIL_SUBJECT)
                .html(BODY_TEXT + ": " + verificationCode)
                .build();

        try {
            CreateEmailResponse data = resend.emails().send(sendEmailRequest);
        } catch (ResendException e) {
            throw new RuntimeException(e);
        }

    }
}
