package com.example.online_bank.controller;

import com.example.online_bank.domain.dto.*;
import com.example.online_bank.service.crud.CrudCurrencyService;
import com.example.online_bank.service.CurrencyConversionService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import static org.springframework.http.HttpStatus.CREATED;

@Tag(name = "Валютный Сервис", description = "Методы по работе с курсами валют")
@RestController
@RequestMapping("/api/currency")
@RequiredArgsConstructor
public class CurrencyController {
    private final CrudCurrencyService crudCurrencyService;
    private final CurrencyConversionService currencyConversionService;

    @PreAuthorize("hasRole('ADMIN')")
    @PostMapping("/create")
    @Operation(summary = "Создать обменный курс")
    @ApiResponse(responseCode = "201", content = @Content(mediaType = "text/plain"))
    public ResponseEntity<RateResponseDto> createExchangeRate(@RequestBody @Valid CreateExchangeRateDto dtoRequest) {
        return ResponseEntity.status(CREATED).body(crudCurrencyService.create(
                dtoRequest.baseCurrency(),
                dtoRequest.targetCurrency(),
                dtoRequest.rate()
        ));
    }

    @PostMapping("/convert")
    @Operation(summary = "Конвертировать валюту")
    public ConvertCurrencyResponseDto convertCurrency(@RequestBody ConvertCurrencyRequestDto dtoRequest) {
        return currencyConversionService.convert(dtoRequest.baseCurrency(), dtoRequest.targetCurrency(), dtoRequest.providedAmountInBaseCurrency());
    }

    @PostMapping("/find-rate")
    @Operation(summary = "Найти курс")
    public RateResponseDto findRate(@RequestBody RateRequestDto dto) {
        return crudCurrencyService.findRate(dto);
    }
}
