package com.example.online_bank.controller;

import com.example.online_bank.domain.dto.*;
import com.example.online_bank.service.AuthenticationService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import static org.springframework.http.HttpStatus.CREATED;

@RestController
@RequestMapping("/api")
@RequiredArgsConstructor
@Tag(name = "Контроллер аутентификации")
public class AuthenticationController {
    private final AuthenticationService authenticationService;

    /**
     * Верификация для нового пользователя по электронной почте,
     *
     * @return возвращает токен пользователя
     */
    @PostMapping("first-verify/email")
    @Operation(summary = "Верификация")
    @ApiResponse(responseCode = "200",
            content = @Content(mediaType = "text/plain", schema = @Schema(implementation = String.class))
    )
    public ResponseEntity<AuthenticationResponseDto> firstVerification(@RequestBody VerificationRequestDto dtoRequest) {
        return ResponseEntity.status(CREATED.value())
                .body(authenticationService.firstVerification(dtoRequest));
    }

    /**
     * Верификация для существующего пользователя по электронной почте,
     *
     * @return возвращает токен пользователя
     */
    @PostMapping("default-verify/email")
    @Operation(summary = "Верификация")
    @ApiResponse(responseCode = "200",
            content = @Content(mediaType = "text/plain", schema = @Schema(implementation = String.class))
    )
    public ResponseEntity<AuthenticationResponseDto> defaultVerification(@RequestBody VerificationRequestDto dtoRequest) {
        return ResponseEntity.status(CREATED.value())
                .body(authenticationService.defaultVerification(dtoRequest));
    }

    @PostMapping("/silent")
    public ResponseEntity<AuthenticationResponseDto> silentLogin(@RequestBody SilentLoginRequestDto dto) {
        return ResponseEntity.status(200).body(authenticationService.silentLogin(dto));
    }

    //если входим со старого/нового устройства и пароль с почтой верный, то добавляем устройство в семью токенов
    @PostMapping("/login")
    public ResponseEntity<AuthenticationResponseDto> login(@RequestBody LoginRequestDto dto) {
        return ResponseEntity.status(200).body(authenticationService.login(dto));
    }

    @PostMapping("/logout")
    public ResponseEntity<Void> logout(@RequestBody LogoutRequestDto dto) {
        authenticationService.logout(dto);
        return ResponseEntity.ok().build();
    }
}
