package com.example.online_bank.config;

import com.example.online_bank.security.jwt.service.SecretKeyManager;
import jakarta.annotation.PostConstruct;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.ToString;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import javax.crypto.SecretKey;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.time.Duration;

@Slf4j
@Component
@RequiredArgsConstructor
@Getter
@ToString
public class JwtConfig {
    @Value("${jwt.lifetime}")
    private Duration accessTokenLifetime;

    @Value("${jwt.refresh-token-life-time}")
    private Duration refreshAndIdTokenLifetime;

    @Value("${jwt.not-before}")
    private Duration notBeforeTime;

    @Value("${jwt.secret-file-name}")
    private String fileName;

    @Value("${jwt.audience}")
    private String audience;

    @Value("${jwt.issuer}")
    private String issuer;

    private SecretKey key;

    private final SecretKeyManager secretKeyManager;

    /**
     * Файл есть?
     * <p>
     * Да:
     * <p>
     * 1) {@code decodeFile} - считывает преобразованные байты с файла ({@code  readKeyFromFile})
     * и преобразует в SecretKey(decode):
     * <p>
     * 1.1) {@code readKeyFromFile} - читаем строку из байтов из указанного файла
     * <p>
     * 1.2) {@code decode}  - декодирует строку в массив байтов с использованием схемы кодирования Base64
     * <p>
     * 1.3) {@code return hmacShaKeyFor} - Создает SecretKey на основе указанного массива байтов ключа
     * <p>
     * Нет:
     * <p>
     * 1)	Создаем секретный ключ ({@code createSecretKey})
     * <p>
     * 2)	Устанавливаем его в конфиге JWT ({@code this.key = SecretKey })
     * <p>
     * 3)	Кодируем и записываем ключ в файл(3.1 {@code encodeAndWriteKey} ):
     * <p>
     * 3.1) {@code encode}  - получаем байты секретного ключа в строке;
     * <p>
     * 3.1.1){@code encodeToString}  - создаем String из массива байтов с помощью кодировки ISO-8859-1
     * <p>
     * 3.2) {@code writeKeyToFile} - записываем строку из байтов в указанный файл
     */
    @PostConstruct
    public void initSecretKey() {
        log.debug("init secret key");
        try {
            File file = new File(fileName);
            if (file.exists()) {
                this.key = secretKeyManager.decodeFile(file.getName());
                log.debug("Secret key decoded successfully");
            } else {
                SecretKey secretKey = secretKeyManager.createSecretKey();
                this.key = secretKey;
                secretKeyManager.encodeAndWriteKey(new FileWriter(fileName), secretKey);
                log.debug("Secret key has been encoded and written");
            }
        } catch (IOException e) {
            log.error(e.getMessage());
        }
    }
}
