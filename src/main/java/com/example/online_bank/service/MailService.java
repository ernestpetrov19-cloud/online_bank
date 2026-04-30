package com.example.online_bank.service;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
@Setter
@Getter
public class MailService {
    private final JavaMailSender javaMailSender;
    @Value("${spring.mail.username}")
    private String fromTo;

    /**
     * @param to      Адрес получателя (example@gmail.com)
     * @param subject Тема письма (Подтверждение входа/регистрации)
     * @param body    Содержимое письма (Здравствуйте, ваш код подтверждения)
     */
    public void send(String to, String subject, String body) {
        SimpleMailMessage simpleMailMessage = new SimpleMailMessage();
        simpleMailMessage.setTo(to);
        simpleMailMessage.setSubject(subject);
        simpleMailMessage.setText(body);
        simpleMailMessage.setFrom(fromTo);

        javaMailSender.send(simpleMailMessage);
    }
}