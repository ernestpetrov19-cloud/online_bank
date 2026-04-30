package com.example.online_bank.repository;

import com.example.online_bank.domain.entity.DeviceChallenge;
import com.example.online_bank.domain.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;

public interface DeviceChallengesRepository extends JpaRepository<DeviceChallenge, Long> {
    boolean existsByDeviceNameAndDeviceIdAndUserAgentAndUser(
            String deviceName,
            String deviceId,
            String userAgent,
            User user
    );
}
