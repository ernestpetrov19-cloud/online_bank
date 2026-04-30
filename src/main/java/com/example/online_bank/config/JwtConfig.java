package com.example.online_bank.config;

import lombok.*;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.context.properties.ConfigurationProperties;

import javax.crypto.SecretKey;
import java.time.Duration;

@Slf4j
@ConfigurationProperties(prefix = "jwt")
@RequiredArgsConstructor
@Getter
@Setter
@ToString
public class JwtConfig {
    @NonNull
    private Duration accessTokenLifetime;
    @NonNull
    private Duration refreshTokenLifetime;
    @NonNull
    private Duration idTokenLifetime;
    @NonNull
    private Duration notBefore;
    @NonNull
    private String fileName;
    @NonNull
    private String audience;
    @NonNull
    private String issuer;
    private SecretKey key;
}
