package com.example.online_bank.service;

import com.example.online_bank.domain.dto.ConvertCurrencyResponseDto;
import com.example.online_bank.domain.dto.FinanceOperationDto;
import com.example.online_bank.domain.dto.OperationInfoDto;
import com.example.online_bank.enums.CurrencyCode;
import com.example.online_bank.mapper.OperationMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;

import static com.example.online_bank.enums.OperationType.DEPOSIT;
import static com.example.online_bank.enums.OperationType.WITHDRAW;

@Service
@RequiredArgsConstructor
public class BankService {
    private final AccountService accountService;
    private final OperationService operationService;
    private final OperationMapper operationMapper;
    private final CurrencyConversionService currencyConversionService;

    /**
     * Делать платеж:
     * <p>
     * Проверяем что счет принадлежит пользователю.
     * Производит списание со счета. Записывает операцию в историю
     *
     * @param dto Номер счета, выбранный код валюты, описание, количестве денег
     * @return Возвращает информацию об операции списания со счета
     */
    @Transactional()
    public OperationInfoDto makePayment(FinanceOperationDto dto) {
        CurrencyCode accountCurrencyCode = accountService.findCurrencyCode(dto.accountNumber());

        ConvertCurrencyResponseDto convertedResult = currencyConversionService.convert(
                dto.selectedCurrencyCode(),
                accountCurrencyCode,
                dto.amount()
        );

        accountService.withdrawMoney(dto.accountNumber(), convertedResult.targetConvertedAmount());


        return operationMapper.toOperationInfoDto(operationService.createOperation(
                LocalDateTime.now(),
                WITHDRAW,
                dto.amount(),
                dto.description(),
                dto.accountNumber(),
                dto.selectedCurrencyCode())
        );
    }

    /**
     * Делать зачисление: на вход - номер счета, сумма, описание.
     * Зачисляет на банковский счет деньги и записывает операцию в историю.
     *
     * @param dto Содержит информацию о номере счета, код валюты, описание, количестве денег
     * @return Возвращает информацию об операции пополнении счета
     */

    @Transactional()
    public OperationInfoDto makeDeposit(FinanceOperationDto dto) {
         CurrencyCode accountCurrencyCode = accountService.findCurrencyCode(dto.accountNumber());

        ConvertCurrencyResponseDto convertedResult = currencyConversionService.convert(
                dto.selectedCurrencyCode(),
                accountCurrencyCode,
                dto.amount()
        );

        accountService.depositMoney(dto.accountNumber(), convertedResult.targetConvertedAmount());

        //TODO перевести на ивенты
        return operationMapper.toOperationInfoDto(operationService.createOperation(
                LocalDateTime.now(),
                DEPOSIT,
                dto.amount(),
                dto.description(),
                dto.accountNumber(),
                dto.selectedCurrencyCode())
        );
    }
}