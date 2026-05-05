package com.example.online_bank.service;

import com.example.online_bank.domain.dto.BuyCurrencyDto;
import com.example.online_bank.domain.dto.FinanceOperationDto;
import com.example.online_bank.domain.dto.OperationInfoDto;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

import static com.example.online_bank.enums.CurrencyCode.RUB;
import static com.example.online_bank.enums.OperationType.DEPOSIT;
import static com.example.online_bank.enums.OperationType.WITHDRAW;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class BuyCurrencyServiceTest {
    @InjectMocks
    private BuyCurrencyService buyCurrencyService;
    @Mock
    private AccountService accountService;
    @Mock
    private BankService bankService;

    @Test
    void successBuyCurrency() {
        BuyCurrencyDto dto = new BuyCurrencyDto("001", "002", BigDecimal.valueOf(100));
        when(accountService.findCurrencyCode("002")).thenReturn((RUB));
        List<String> descriptions = List.of(
                "Продажа валюты со счета %s".formatted(dto.baseAccountNumber()),
                "Покупка валюты со счета %s".formatted(dto.targetAccountNumber())
        );

        final String paymentDescription = descriptions.getFirst();
        final String depositDescription = descriptions.getLast();

        LocalDateTime now = LocalDateTime.now();
        OperationInfoDto paymentOperationDto = new OperationInfoDto(
                1L,
                now,
                dto.baseAccountNumber(),
                WITHDRAW,
                dto.amount(),
                paymentDescription,
                RUB);

        OperationInfoDto depositOperationDto = new OperationInfoDto(
                1L,
                now,
                dto.targetAccountNumber(),
                DEPOSIT,
                dto.amount(),
                depositDescription,
                RUB
        );

        var paymentDto = new FinanceOperationDto(
                dto.baseAccountNumber(),
                dto.amount(),
                paymentDescription,
                RUB);

        var depositDto = new FinanceOperationDto(
                dto.targetAccountNumber(),
                dto.amount(),
                depositDescription,
                RUB);

        when(bankService.makePayment(paymentDto)
        ).thenReturn(paymentOperationDto);

        when(bankService.makeDeposit(depositDto)
        ).thenReturn(depositOperationDto);

        List<OperationInfoDto> operationDtoResponses = buyCurrencyService.buyCurrency(dto);
        OperationInfoDto operationPaymentDto = operationDtoResponses.getFirst();


        assertNotNull(operationDtoResponses);
        assertEquals(paymentDescription, operationPaymentDto.description());
        assertEquals(RUB, operationPaymentDto.currencyCode());
        assertEquals("001", operationPaymentDto.accountNumber());
    }
}