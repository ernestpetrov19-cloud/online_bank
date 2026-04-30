package com.example.online_bank.service.impl;

import com.example.online_bank.service.MailService;
import com.example.online_bank.service.NotificationService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service("defaultNotificationService")
@RequiredArgsConstructor
public class DefaultEmailNotificationService implements NotificationService {
    private final MailService mailService;

    @Override
    public void sendVerificationCode(String to, String subject, String body) {
        mailService.send(to, subject, body);
    }
}
