package com.example.online_bank.service;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.springframework.mail.MailSender;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.test.context.junit.jupiter.SpringExtension;

@ExtendWith(SpringExtension.class)
class MailServiceTest {
    @InjectMocks
    private MailService mailService;
    @Mock
    private MailSender mailSender;
    @Mock
    private JavaMailSender javaMailSender;

    @Test
    void successSendMail() {
        mailService.setFromTo("test@gmail.com");
        String to = "test2@mail.com";

        Assertions.assertNotNull(mailService.getFromTo());
        Assertions.assertDoesNotThrow(() -> mailService.send(to, "qwer", "qwe"));
    }

}