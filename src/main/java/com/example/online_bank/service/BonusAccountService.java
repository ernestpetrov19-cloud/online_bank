package com.example.online_bank.service;

import com.example.online_bank.domain.dto.FinanceOperationDto;
import com.example.online_bank.domain.dto.OperationInfoDto;
import com.example.online_bank.domain.entity.BonusAccount;
import com.example.online_bank.enums.CurrencyCode;
import com.example.online_bank.exception.ConvertBonusException;
import com.example.online_bank.repository.BonusAccountRepository;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;

@Service
@RequiredArgsConstructor
@Slf4j
public class BonusAccountService {
    private final BonusAccountRepository bonusAccountRepository;
    private final BankService bankService;

    public BonusAccount getBonusAccountByAccountNumber(String accountNumber) {
        return bonusAccountRepository.findByAccount_AccountNumber(accountNumber).orElseThrow(EntityNotFoundException::new);
    }

    public OperationInfoDto convertPoints(String accountNumber, BigDecimal amount) {
        BonusAccount bonusAccount = bonusAccountRepository.findByAccount_AccountNumber(accountNumber).orElseThrow(
                () -> new EntityNotFoundException("Счет не существует")
        );

        if (amount.compareTo(bonusAccount.getPoints()) > 0) {
            throw new ConvertBonusException("Не хватает бонусов для выполнения операции. Убедитесь, что введенное количество не больше очков");
        }

        BigDecimal convertResult = amount.multiply(BigDecimal.valueOf(0.5));
        FinanceOperationDto operationDto = new FinanceOperationDto(accountNumber, convertResult, "Пополнение бонусов", CurrencyCode.RUB);
        return bankService.makeDeposit(operationDto);
    }

    public void depositBonus(String accountNumber, Integer points) {
        BonusAccount bonusAccount = getBonusAccountByAccountNumber(accountNumber);
        bonusAccount.setPoints(BigDecimal.valueOf(points));

        bonusAccountRepository.save(bonusAccount);
        log.info("deposit bonus account: {}", bonusAccount);
    }
}
