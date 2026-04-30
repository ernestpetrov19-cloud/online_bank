package com.example.online_bank.controller;

import com.example.online_bank.service.NotificationService;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;

@RestController
@RequestMapping("/api/test")
public class TestController {
    @Qualifier("resendService")
    private final NotificationService notificationService;

    public TestController(@Qualifier("resendService") NotificationService notificationService) {
        this.notificationService = notificationService;
    }

    @PreAuthorize("hasRole('ADMIN')")
    @GetMapping
    public ResponseEntity<String> test() {
        return ResponseEntity.status(201).body("test");
    }

    @PreAuthorize("hasRole('ADMIN')")
    @GetMapping("/pure")
    public void pureJava(HttpServletResponse response) throws IOException {
        response.getWriter().write("PURE-JAVA-WORKS");
        response.getWriter().flush();
    }

    @PreAuthorize("hasRole('ADMIN')")
    @PostMapping("/send-email")
    public void sendEmail(@RequestParam String email) {
        notificationService.sendVerificationCode(email, "HELLO WORLD", "");
    }
}
