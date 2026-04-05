package com.example.online_bank.service.listener;

import com.example.online_bank.domain.event.SendOtpEvent;
import com.example.online_bank.service.NotificationService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.event.EventListener;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
@Slf4j
public class SendOtpEventListener {
    @Qualifier("resendService")
    private final NotificationService notificationService;

    @EventListener
    @Async
    public void onSendOtpEventListener(SendOtpEvent event) {
        log.info("Отправка сообщения на почту");
        notificationService.sendOtpCode(event.email(), event.code(), event.bodyText());
    }
}
