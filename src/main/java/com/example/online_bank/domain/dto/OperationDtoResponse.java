package com.example.online_bank.domain.dto;

import com.example.online_bank.enums.CurrencyCode;
import com.example.online_bank.enums.OperationType;

import java.time.LocalDateTime;

/**
 * Данное dto используется для получения ответа от сервера.
 * Н-р: после снятия или пополнения показывать счет до операции и после
 *
 * @param accountNumber Номер счета
 * @param createdAt     Время операции
 * @param operationId   id операции
 * @param operationType Тип операции
 * @param description   Описание операции
 * @param currencyCode  Код валюты
 */
public record OperationDtoResponse(
        String accountNumber,
        LocalDateTime createdAt,
        Long operationId,
        OperationType operationType,
        String description,
        CurrencyCode currencyCode
) {
}
