package com.example.online_bank.service.listener;

import com.example.online_bank.domain.event.SendVerificationCodeEvent;
import com.example.online_bank.service.NotificationService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;
import org.springframework.transaction.event.TransactionalEventListener;

@Component
@Slf4j
public class SendOtpEventListener {
    private final NotificationService notificationService;

    public SendOtpEventListener(@Qualifier("defaultNotificationService") NotificationService notificationService) {
        this.notificationService = notificationService;
    }

    @TransactionalEventListener
    @Async
    public void onSendOtpEventListener(SendVerificationCodeEvent event) {
        log.trace("Отправка сообщения на почту");
        notificationService.sendVerificationCode(
                event.userEmail(),
                event.subjectMessage().getValue(),
                event.bodyMessage().getValue() + event.code()
        );
    }
}
