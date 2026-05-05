package com.example.online_bank.service;

import com.example.online_bank.domain.dto.ConvertCurrencyResponseDto;
import com.example.online_bank.domain.dto.FinanceOperationDto;
import com.example.online_bank.domain.dto.OperationInfoDto;
import com.example.online_bank.domain.entity.Account;
import com.example.online_bank.domain.entity.Operation;
import com.example.online_bank.mapper.OperationMapper;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import java.time.LocalDateTime;

import static com.example.online_bank.enums.CurrencyCode.RUB;
import static com.example.online_bank.enums.OperationType.DEPOSIT;
import static com.example.online_bank.enums.OperationType.WITHDRAW;
import static java.math.BigDecimal.TEN;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.when;

@ExtendWith(SpringExtension.class)
@Slf4j
class BankServiceTest {
    @InjectMocks
    private BankService bankService;
    @Mock
    private AccountService accountService;
    @Mock
    private OperationService operationService;
    @Mock
    private OperationMapper operationMapper;
    @Mock
    private CurrencyConversionService currencyConversionService;

    @Test
    void successMakePayment_RequestCurrencyMatchAccount() {
        //Подготовка данных
        FinanceOperationDto dtoRq = new FinanceOperationDto("001", TEN, "test", RUB);
        Account accountMock = Account.builder().accountNumber("001").currencyCode(RUB).build();
        Operation operation = Operation.builder()
                .id(1L)
                .description(dtoRq.description())
                .createdAt(LocalDateTime.now())
                .amount(dtoRq.amount())
                .currencyCode(dtoRq.selectedCurrencyCode())
                .operationType(WITHDRAW)
                .account(accountMock)
                .build();

        OperationInfoDto operationResponseMock = new OperationInfoDto(
                operation.getId(),
                operation.getCreatedAt(),
                operation.getAccount().getAccountNumber(),
                operation.getOperationType(),
                operation.getAmount(),
                operation.getDescription(),
                operation.getCurrencyCode()
        );

        ConvertCurrencyResponseDto convertResponse = new ConvertCurrencyResponseDto(RUB, RUB, TEN, TEN);

        when(accountService.findCurrencyCode("001")).thenReturn((RUB));

        when(currencyConversionService.convert(
                dtoRq.selectedCurrencyCode(),
                dtoRq.selectedCurrencyCode(),
                dtoRq.amount()
        )).thenReturn(convertResponse);

        when(operationService.createOperation
                (any(LocalDateTime.class),
                        eq(WITHDRAW),
                        eq(TEN),
                        eq(dtoRq.description()),
                        eq("001"),
                        eq(RUB)
                )).thenReturn(operation);
        when(operationMapper.toOperationInfoDto(any(Operation.class))).thenReturn(operationResponseMock);
        OperationInfoDto result = bankService.makePayment(dtoRq);

        assertNotNull(result);
        assertEquals(dtoRq.accountNumber(), result.accountNumber());
        assertEquals(TEN, result.amount());
        assertEquals(RUB, result.currencyCode());
    }

    @Test
    void successMakeDeposit() {
        //Подготовка данных
        FinanceOperationDto dtoRq = new FinanceOperationDto("001", TEN, "test", RUB);
        Account accountMock = Account.builder().accountNumber("001").currencyCode(RUB).build();
        Operation operation = Operation.builder()
                .id(1L)
                .description(dtoRq.description())
                .createdAt(LocalDateTime.now())
                .amount(dtoRq.amount())
                .currencyCode(dtoRq.selectedCurrencyCode())
                .operationType(DEPOSIT)
                .account(accountMock)
                .build();

        OperationInfoDto operationResponseMock = new OperationInfoDto(
                operation.getId(),
                operation.getCreatedAt(),
                operation.getAccount().getAccountNumber(),
                operation.getOperationType(),
                operation.getAmount(),
                operation.getDescription(),
                RUB
        );
        ConvertCurrencyResponseDto convertResponse = new ConvertCurrencyResponseDto(RUB, RUB, TEN, TEN);

        when(accountService.findCurrencyCode("001")).thenReturn((RUB));
        when(currencyConversionService.convert(
                dtoRq.selectedCurrencyCode(),
                dtoRq.selectedCurrencyCode(),
                dtoRq.amount()
        )).thenReturn(convertResponse);

        when(operationService.createOperation(
                any(LocalDateTime.class),
                eq(DEPOSIT),
                eq(TEN),
                eq(dtoRq.description()),
                eq("001"),
                eq(RUB)
        )).thenReturn(operation);
        when(operationMapper.toOperationInfoDto(any(Operation.class))).thenReturn(operationResponseMock);
        OperationInfoDto result = bankService.makeDeposit(dtoRq);
        assertNotNull(result);
        assertEquals(dtoRq.accountNumber(), result.accountNumber());
        assertEquals(TEN, result.amount());
        assertEquals(RUB, result.currencyCode());
    }
}