package com.example.online_bank.service;


public interface NotificationService {

    /**
     * @param to      Кому отправить письмо
     * @param subject Заголовок письма
     * @param body    Содержимое письма
     */
    void sendVerificationCode(String to, String subject, String body);
}