package com.example.online_bank.mapper;

import com.example.online_bank.domain.dto.OperationInfoDto;
import com.example.online_bank.domain.entity.Account;
import com.example.online_bank.domain.entity.Operation;
import com.example.online_bank.enums.OperationType;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

import static com.example.online_bank.enums.CurrencyCode.RUB;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

@Slf4j
@ExtendWith(SpringExtension.class)
class OperationMapperTest {
    private final OperationMapper operationMapper = new OperationMapperImpl();

    @Test
    void toOperationInfoDto() {
        //arrange(подготовка данных)
        LocalDateTime createdAt = LocalDateTime.of(2025, 10, 22, 10, 55);
        Operation operation = Operation.builder()
                .id(1L)
                .createdAt(createdAt)
                .operationType(OperationType.DEPOSIT)
                .amount(new BigDecimal("100"))
                .description("test")
                .currencyCode(RUB)
                .build();

        Account account = Account.builder()
                .accountNumber("0000001111100000")
                .balance(BigDecimal.valueOf(1000))
                .currencyCode(RUB)
                .isBlocked(false)
                .operations(List.of(operation))
                .build();

        operation.setAccount(account);

        //act
        OperationInfoDto operationInfoDto = operationMapper.toOperationInfoDto(operation);
        log.info(operationInfoDto.toString());

        assertNotNull(operationInfoDto);
        assertEquals("0000001111100000", operationInfoDto.accountNumber());
        assertEquals(1L, operationInfoDto.id());
        assertEquals(createdAt, operationInfoDto.createdAt());
        assertEquals(OperationType.DEPOSIT, operationInfoDto.operationType());
        assertEquals(BigDecimal.valueOf(100), operationInfoDto.amount());
        assertEquals("test", operationInfoDto.description());
        assertEquals(RUB, operationInfoDto.currencyCode());
    }
}