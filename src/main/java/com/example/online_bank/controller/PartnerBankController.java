package com.example.online_bank.controller;

import com.example.online_bank.domain.dto.BankPartnerDto;
import com.example.online_bank.service.BankPartnerService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/bank-partner")
@RequiredArgsConstructor
public class PartnerBankController {
    private final BankPartnerService bankPartnerService;

    @PreAuthorize("hasRole('ADMIN')")
    @PostMapping
    public void create(@RequestBody BankPartnerDto dto) {
        bankPartnerService.create(dto.name(), dto.category());
    }

    @GetMapping
    public List<BankPartnerDto> getAll() {
        return bankPartnerService.getAll();
    }
}
