package com.example.online_bank.util;

import com.example.online_bank.service.UserService;
import jakarta.annotation.PreDestroy;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
@Slf4j
@Profile("test")
public class DataBaseCleaner {
    private final UserService userService;

    @PreDestroy
    public void cleanup() {
        log.info("Cleaning up database...");
        userService.truncateAll();
    }
}
