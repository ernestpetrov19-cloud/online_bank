package com.example.online_bank.repository;

import com.example.online_bank.domain.entity.Account;
import com.example.online_bank.enums.CurrencyCode;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface AccountRepository extends JpaRepository<Account, Long> {

    Optional<Account> findByAccountNumber(String accountNumber);

    List<Account> findAllByHolderUuid(UUID holderUuid);

    @Query("select a.balance from Account a where a.accountNumber = :accountNumber")
    Optional<BigDecimal> findBalanceByAccountNumber(String accountNumber);

    @Query("select a.currencyCode from Account a where a.accountNumber = :accountNumber")
    Optional<CurrencyCode> findCurrencyCodeByAccountNumber(String accountNumber);

    boolean existsByAccountNumberAndHolder_Uuid(String accountNumber, UUID holderUuid);

    boolean existsByAccountNumber(String accountNumber);

}
