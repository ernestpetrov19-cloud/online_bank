package com.example.online_bank.util;

import com.example.online_bank.domain.dto.RegistrationDtoRequest;
import com.example.online_bank.domain.entity.User;
import com.example.online_bank.service.QuestService;
import com.example.online_bank.service.RegistrationService;
import com.example.online_bank.service.UserQuestService;
import com.example.online_bank.service.UserService;
import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Component;

import java.util.Optional;

@Component
@RequiredArgsConstructor
@Profile("test")
public class FillDb {
    private final QuestService questService;
    private final UserQuestService userQuestService;
    private final UserService userService;
    private final RegistrationService registrationService;

    @PostConstruct
    private void fillQuest() {
        questService.createRandomQuest();
        Optional<User> byEmail = userService.findByEmail("gilmanovamir19@gmail.com");
        if (byEmail.isEmpty()) {
            registrationService.signUp(new RegistrationDtoRequest("a", "a", "a", "a", "1234", "gilmanovamir19@gmail.com"));
        }
        User user = userService.findByEmail("gilmanovamir19@gmail.com").orElseThrow(RuntimeException::new);
        userQuestService.makeRelationBetweenUserAndQuest(user);
    }
}
