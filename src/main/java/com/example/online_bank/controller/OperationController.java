package com.example.online_bank.controller;

import com.example.online_bank.domain.dto.OperationDtoResponse;
import com.example.online_bank.domain.dto.OperationInfoDto;
import com.example.online_bank.security.userdetails.JwtUserDetails;
import com.example.online_bank.service.OperationService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/operation")
@RequiredArgsConstructor
@Tag(name = "Контроллер финансовых операций", description = "Методы финансовых операций внутри банка")
public class OperationController {
    private final OperationService operationService;

    /**
     * Найти все операции по номеру счету
     */
    @Operation(summary = "Найти все операции по номеру счета")
    @ApiResponse(
            responseCode = "200",
            content = @Content(
                    mediaType = "application/json",
                    schema = @Schema(implementation = OperationDtoResponse.class)
            )
    )
    @GetMapping("/find-all-by-account-number")
    @PreAuthorize("@accountSecurity.isOwner(#accountNumber, authentication.principal.uuid)")
    public List<OperationInfoDto> findByAccountNumber(
            @RequestParam String accountNumber,
            @RequestParam int page,
            @RequestParam int size) {
        return operationService.findAllByAccount(accountNumber, page, size);
    }

    /**
     * Найти все операции пользователя(порционно)
     * </p>
     *
     * @return вернёт список всех операций для пользователя
     */
    @GetMapping("/find-all-operation-by-verifiedUser")
    @Operation(summary = "Просмотреть список всех операций")
    @ApiResponse(responseCode = "200",
            content = @Content(
                    mediaType = "application/json",
                    schema = @Schema(implementation = Operation.class))
    )
    public List<OperationInfoDto> getAllUserOperations(
            @Parameter(description = "Uuid пользователя", example = "4c314d57-cbd0-4a83-9ce3-943e95b277a9")
            @AuthenticationPrincipal JwtUserDetails userDetails,

            @RequestParam
            @Parameter(description = "Страница начала показа операций", example = "5")
            int page,

            @RequestParam
            @Parameter(description = "Размер страницы", example = "10")
            int size
    ) {
        return operationService.findAllByUserPaged(UUID.fromString(userDetails.getUuid()), page, size);
    }
}
