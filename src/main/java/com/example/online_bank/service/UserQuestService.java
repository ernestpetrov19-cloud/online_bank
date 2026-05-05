package com.example.online_bank.service;

import com.example.online_bank.domain.entity.Quest;
import com.example.online_bank.domain.entity.User;
import com.example.online_bank.domain.entity.UserQuest;
import com.example.online_bank.domain.event.RelatableUserToQuestEvent;
import com.example.online_bank.repository.UserQuestRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.List;

@Service
@RequiredArgsConstructor
public class UserQuestService {
    private final UserQuestRepository userQuestRepository;
    private final QuestService questService;

    public void create(Quest quest, User user) {
        UserQuest.builder()
                .quest(quest)
                .user(user)
                .build();
    }

    public void saveAll(List<UserQuest> userQuests) {
        userQuestRepository.saveAll(userQuests);
    }

    @EventListener
    public void initializeQuestsForNewUser(RelatableUserToQuestEvent event) {
        List<Quest> allAvailable = questService.findAllAvailable(LocalDate.now());
        List<UserQuest> userQuests = allAvailable.stream()
                .map(quest -> UserQuest.builder()
                        .quest(quest)
                        .user(event.user())
                        .isComplete(false)
                        .build()
                )
                .toList();
        saveAll(userQuests);
    }
}
