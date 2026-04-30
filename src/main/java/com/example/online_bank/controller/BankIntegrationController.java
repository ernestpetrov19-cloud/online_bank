package com.example.online_bank.controller;

import com.example.online_bank.domain.dto.OperationDtoResponse;
import com.example.online_bank.domain.dto.TransferDto;
import com.example.online_bank.service.TransferService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/integration")
@RequiredArgsConstructor
@Tag(name = "Интеграция банков", description = "Методы по рабе интеграции между банками")
public class BankIntegrationController {
    private final TransferService transferService;

    @GetMapping("/get-bank-info")
    @Operation(summary = "Получить название текущего банка")
    @ApiResponse(responseCode = "200",
            content = @Content(
                    mediaType = "text/plain",
                    schema = @Schema(implementation = String.class, example = "EuroBank")
            )
    )
    public String info() {
        return transferService.getBankInfo();
    }

    /**
     * Сделать перевод другому клиенту
     *
     * @param dto содержит в себе номер счета получателя и отправителя, номера их банков
     * @return информацию изменения баланса получателя
     */
    @Operation(summary = "Сделать перевод в другой банк")
    @PostMapping("/transfer")
    @ApiResponse(responseCode = "200",
            content = @Content(
                    mediaType = "application/json",
                    schema = @Schema(implementation = OperationDtoResponse.class))
    )
    public OperationDtoResponse transfer(@RequestBody TransferDto dto) {
        return transferService.transferMoney(dto);
    }
}

