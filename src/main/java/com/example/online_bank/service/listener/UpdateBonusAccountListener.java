package com.example.online_bank.service.listener;

import com.example.online_bank.domain.event.UpdateBonusAccountEvent;
import com.example.online_bank.service.BonusAccountService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.event.EventListener;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;

@Slf4j
@Component
@RequiredArgsConstructor
public class UpdateBonusAccountListener {
    private final BonusAccountService bonusAccountService;

    @EventListener
    @Async
    public void onUpdateBonusAccountEvent(UpdateBonusAccountEvent event) {
        log.info("Пополнение бонусного счета");
        bonusAccountService.depositBonus(event.accountNumber(), event.points());
    }
}
