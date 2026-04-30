package com.example.online_bank.repository;

import com.example.online_bank.domain.entity.TrustedDevice;
import com.example.online_bank.domain.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface TrustedDeviceRepository extends JpaRepository<TrustedDevice, Long> {
    Optional<TrustedDevice> findByDeviceNameAndDeviceIdAndUser(String deviceName, String deviceId, User user);

    void deleteByDeviceIdAndUser(String deviceId, User user);
}
