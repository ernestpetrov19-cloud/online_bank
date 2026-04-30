package com.example.online_bank.service;

import com.example.online_bank.domain.entity.TrustedDevice;
import com.example.online_bank.domain.entity.User;
import com.example.online_bank.repository.TrustedDeviceRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.Optional;

@Service
@RequiredArgsConstructor
@Slf4j
public class TrustedDeviceService {
    private final TrustedDeviceRepository trustedDeviceRepository;

    public TrustedDevice create(
            String deviceName,
            String deviceId,
            User user,
            String userAgent) {
        TrustedDevice trustedDevice = TrustedDevice.builder()
                .deviceName(deviceName)
                .deviceId(deviceId)
                .userAgent(userAgent)
                .createdAt(LocalDateTime.now())
                .user(user)
                .build();

        trustedDeviceRepository.save(trustedDevice);
        return trustedDevice;
    }

    public Optional<TrustedDevice> findByParam(
            String deviceName,
            String deviceId,
            User user
    ) {
        return trustedDeviceRepository.findByDeviceNameAndDeviceIdAndUser(
                deviceName,
                deviceId,
                user
        );
    }

    public void updateUserAgent(String userAgent, TrustedDevice trustedDevice) {
        trustedDevice.setUserAgent(userAgent);
        trustedDeviceRepository.save(trustedDevice);
    }

    public void deleteByUserAndDeviceId(String deviceId, User user) {
        trustedDeviceRepository.deleteByDeviceIdAndUser(deviceId, user);
    }
}
