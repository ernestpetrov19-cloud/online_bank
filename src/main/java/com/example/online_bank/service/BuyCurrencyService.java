package com.example.online_bank.service;

import com.example.online_bank.domain.dto.BuyCurrencyDto;
import com.example.online_bank.domain.dto.FinanceOperationDto;
import com.example.online_bank.domain.dto.OperationInfoDto;
import com.example.online_bank.enums.CurrencyCode;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
@Slf4j
public class BuyCurrencyService {
    private final AccountService accountService;
    private final BankService bankService;
    /**
     * Покупка валюты. Производит списание суммы со счета {@code dto.baseTargetAccount},
     * делает конвертацию в валюту {@code dto.targetAccountNumber}
     */
    @Transactional()
    public List<OperationInfoDto> buyCurrency(BuyCurrencyDto dto) {
        CurrencyCode targetCurrencyCode = accountService.findCurrencyCode(dto.targetAccountNumber());

        List<String> descriptions = createDescriptions(dto);

        final String paymentDescription = descriptions.getFirst();
        final String depositDescription = descriptions.getLast();

        OperationInfoDto paymentOperation = bankService.makePayment(
                new FinanceOperationDto(dto.baseAccountNumber(),
                        dto.amount(),
                        paymentDescription,
                        targetCurrencyCode)
        );

        OperationInfoDto depositOperation = bankService.makeDeposit(new FinanceOperationDto(
                dto.targetAccountNumber(),
                dto.amount(),
                depositDescription,
                targetCurrencyCode
        ));
        return List.of(paymentOperation, depositOperation);
    }

    private List<String> createDescriptions(BuyCurrencyDto dto) {
        String baseAccountPostfix = "валюты со счета %s".formatted(dto.baseAccountNumber());
        String targetAccountPostfix = "валюты со счета %s".formatted(dto.targetAccountNumber());
        return List.of("Продажа %s".formatted(baseAccountPostfix), "Покупка %s".formatted(targetAccountPostfix));
    }
}
