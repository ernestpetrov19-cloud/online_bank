package com.example.online_bank.service;

import com.example.online_bank.domain.entity.User;
import com.example.online_bank.domain.entity.UserCategoryStats;
import com.example.online_bank.domain.event.UpdateUserStatEvent;
import com.example.online_bank.repository.UserCategoryStatsRepository;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.YearMonth;
import java.util.Optional;

import static com.example.online_bank.enums.PartnerCategory.ENTERTAINMENT;
import static java.math.BigDecimal.TEN;
import static java.time.Month.FEBRUARY;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.verify;

@ExtendWith(MockitoExtension.class)
@Slf4j
class UserCategoryStatsServiceTest {
    @InjectMocks
    private UserCategoryStatsService userCategoryStatsService;
    @Mock
    private UserCategoryStatsRepository userCategoryStatsRepository;
    private UpdateUserStatEvent event;

    @BeforeEach
    void setUp() {
        event = new UpdateUserStatEvent(
                User.builder()
                        .id(1L).build(),
                ENTERTAINMENT,
                TEN,
                LocalDate.of(2026, FEBRUARY, 1), "1234"
        );
    }

    @Test
    void successUpdateUserStat_NeverSpendBefore() {
        UserCategoryStats userCategoryStats = userCategoryStatsService.updateUserStat(event);
        log.info("userCategoryStats = {}", userCategoryStats);
        verify(userCategoryStatsRepository).save(userCategoryStats);
        assertNotNull(userCategoryStats);
        assertNotNull(userCategoryStats.getCategory());
        assertNotNull(userCategoryStats.getCountSpendInMonth());
        assertNotNull(userCategoryStats.getSpendPeriod());
        assertNotNull(userCategoryStats.getTotalSpend());
    }

    @Test
    void successUpdateUserStat() {
        int month = event.operationDate().getMonth().getValue();
        int year = event.operationDate().getYear();
        int lengthOfMonth = YearMonth.of(year, month).lengthOfMonth();
        LocalDate start = LocalDate.of(year, month, 1);
        LocalDate end = LocalDate.of(year, month, lengthOfMonth);
        UserCategoryStats arrange = UserCategoryStats
                .builder()
                .category(event.partnerCategory())
                .totalSpend(TEN)
                .spendPeriod(end)
                .user(event.user())
                .countSpendInMonth(1)
                .build();
        Mockito.when(userCategoryStatsRepository.findByUserAndCategoryAndSpendPeriodBetween(
                        Mockito.any(User.class),
                        eq(event.partnerCategory()),
                        eq(start),
                        eq(end)))
                .thenReturn(Optional.of(arrange
                ));

        UserCategoryStats result = userCategoryStatsService.updateUserStat(event);
        verify(userCategoryStatsRepository).save(result);
        log.info("userCategoryStats = {}", result);
        assertNotNull(result);
        assertEquals(result.getCategory(), arrange.getCategory());
        assertEquals(2, result.getCountSpendInMonth());
        assertEquals(BigDecimal.valueOf(20), result.getTotalSpend());
        assertEquals(LocalDate.of(2026, 2, 28), result.getSpendPeriod());
    }
}