package com.example.online_bank.repository;

import com.example.online_bank.domain.entity.BankPartner;
import com.example.online_bank.enums.CurrencyCode;
import com.example.online_bank.enums.PartnerCategory;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;
import java.util.Optional;

public interface BankPartnerRepository extends JpaRepository<BankPartner, Long> {

    @Query("""
            select bp.account.accountNumber
            from BankPartner bp
            where bp.name = :partnerName
            """)
    Optional<String> findAccountNumberByPartnerName(String partnerName);

    @Query("""
             select bp.account.currencyCode
            from BankPartner bp
             where bp.name = :partnerName
            """)
    Optional<CurrencyCode> findCurrencyCodeByPartnerName(String partnerName);

    @Query("""
                select bp.partnerCategory
                from BankPartner  bp
                where bp.name = :name
            """)
    Optional<PartnerCategory> findByNamePartnerCategory(String name);

    List<BankPartner> findTop5ByNameContainingIgnoreCase(String name);
}
