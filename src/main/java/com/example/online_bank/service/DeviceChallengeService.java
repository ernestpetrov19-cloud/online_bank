package com.example.online_bank.service;

import com.example.online_bank.domain.entity.DeviceChallenge;
import com.example.online_bank.domain.entity.User;
import com.example.online_bank.exception.DeviceChallengesNotFoundException;
import com.example.online_bank.repository.DeviceChallengesRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
@Slf4j
public class DeviceChallengeService {
    private final DeviceChallengesRepository deviceChallengesRepository;

    public void create(User user, String deviceName, String deviceId, String userAgent) {
        DeviceChallenge deviceChallenge = DeviceChallenge.builder()
                .user(user)
                .deviceName(deviceName)
                .deviceId(deviceId)
                .userAgent(userAgent)
                .build();
        deviceChallengesRepository.save(deviceChallenge);
    }

    public void existsByParameters(String deviceName, String deviceId, String userAgent, User user) {
        boolean isExists = deviceChallengesRepository.existsByDeviceNameAndDeviceIdAndUserAgentAndUser(
                deviceName,
                deviceId,
                userAgent,
                user
        );

        if (!isExists) {
            log.error("Не удалось найти deviceChallenge");
            throw new DeviceChallengesNotFoundException();
        }
    }
}
