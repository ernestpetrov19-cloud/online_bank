package com.example.online_bank.repository;

import com.example.online_bank.domain.entity.BonusAccount;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface BonusAccountRepository extends JpaRepository<BonusAccount, Long> {
    Optional<BonusAccount> findByAccount_AccountNumber(String accountNumber);
}
