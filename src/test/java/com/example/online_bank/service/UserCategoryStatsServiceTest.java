package com.example.online_bank.service;

import com.example.online_bank.domain.entity.User;
import com.example.online_bank.domain.entity.UserCategoryStats;
import com.example.online_bank.domain.event.UpdateUserStatEvent;
import com.example.online_bank.repository.UserCategoryStatsRepository;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.UUID;

@ExtendWith(MockitoExtension.class)
@Slf4j
class UserCategoryStatsServiceTest {
    @InjectMocks
    private UserCategoryStatsService userCategoryStatsService;
    @Mock
    private UserCategoryStatsRepository userCategoryStatsRepository;
    @Mock
    private UserService userService;
    private User userMock;
    private UpdateUserStatEvent event;
    private UUID uuid;
    private UserCategoryStats userCategoryStats;

    @Test
    void successUpdateUserStat() {

    }
}