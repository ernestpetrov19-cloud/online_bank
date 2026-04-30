package com.example.online_bank.service;

import com.example.online_bank.domain.dto.*;
import com.example.online_bank.domain.model.AbstractBank;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.http.RequestEntity;
import org.springframework.http.ResponseEntity;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.RestTemplate;

import java.math.BigDecimal;
import java.time.LocalDateTime;

import static com.example.online_bank.enums.CurrencyCode.RUB;
import static com.example.online_bank.enums.CurrencyCode.USD;
import static com.example.online_bank.enums.OperationType.WITHDRAW;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class TransferServiceTest {
    @InjectMocks
    private TransferService transferService;
    @Mock
    RestTemplate restTemplate;
    @Mock
    AbstractBank bank;
    @Mock
    AccountService accountService;
    @Mock
    BankService bankService;
    OperationDtoResponse mockBankResponse = new OperationDtoResponse(
            "0002",
            LocalDateTime.now(),
            1L,
            WITHDRAW,
            "test",
            RUB
    );

    TransferDto transferDto = new TransferDto
            (
                    new SenderInfo("0001"),
                    new RecipientInfo(
                            "0002",
                            "TestBankName"
                    ),
                    BigDecimal.valueOf(100),
                    "test"
            );

    @Test
    void successTransferMoney_CurrencyCodeEquals() {
        when(bank.getPrefixUrl()).thenReturn("testUrl");
        when(accountService.findCurrencyCode(Mockito.anyString())).thenReturn(RUB);
        when(bankService.makePayment(any(FinanceOperationDto.class))).thenReturn(mockBankResponse);
        when(restTemplate.exchange(any(RequestEntity.class), eq(OperationDtoResponse.class)))
                .thenReturn(new ResponseEntity<>(mockBankResponse, HttpStatus.CREATED));

        OperationDtoResponse result = transferService.transferMoney(transferDto);
        assertEquals(mockBankResponse, result);
        assertEquals(mockBankResponse.currencyCode(), result.currencyCode());
        verify(accountService, times(1)).findCurrencyCode(anyString());
        verify(bankService, times(1)).makePayment(any(FinanceOperationDto.class));
        verify(restTemplate, times(1)).exchange(any(RequestEntity.class), eq(OperationDtoResponse.class));
        verify(bank, times(1)).getPrefixUrl();
    }

    @Test
    void successTransferMoney_CurrencyCodeDiff() {
        when(bank.getPrefixUrl()).thenReturn("testUrl");
        when(accountService.findCurrencyCode(Mockito.anyString())).thenReturn(USD);
        when(bankService.makePayment(any(FinanceOperationDto.class))).thenReturn(mockBankResponse);
        when(restTemplate.exchange(any(RequestEntity.class), eq(OperationDtoResponse.class)))
                .thenReturn(new ResponseEntity<>(mockBankResponse, HttpStatus.CREATED));

        OperationDtoResponse result = transferService.transferMoney(transferDto);
        assertEquals(mockBankResponse, result);
        assertEquals(mockBankResponse.currencyCode(), result.currencyCode());
        verify(accountService, times(1)).findCurrencyCode(anyString());
        verify(bankService, times(1)).makePayment(any(FinanceOperationDto.class));
        verify(restTemplate, times(1)).exchange(any(RequestEntity.class), eq(OperationDtoResponse.class));
        verify(bank, times(1)).getPrefixUrl();
    }

    @Test
    void failerTransferMoney() {
        when(bank.getPrefixUrl()).thenReturn("testUrl");
        when(accountService.findCurrencyCode(Mockito.anyString())).thenReturn(RUB);
        when(bankService.makePayment(any(FinanceOperationDto.class))).thenReturn(mockBankResponse);
        when(restTemplate.exchange(any(RequestEntity.class), eq(OperationDtoResponse.class)))
                .thenThrow(HttpClientErrorException.class);

        Exception exception = Assertions.assertThrows(Exception.class, () -> transferService.transferMoney(transferDto));
        assertTrue(exception.getMessage().contains("Ошибка при отправке перевода"));
    }

    @Test
    void successGetBankName() {
        when(bank.getName()).thenReturn("testBankName");
        String bankInfo = transferService.getBankInfo();
        assertEquals("testBankName", bankInfo);
    }
}