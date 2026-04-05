package com.example.online_bank.service;

import com.example.online_bank.domain.dto.RegenerateOtpDto;
import com.example.online_bank.domain.entity.TrustedDevice;
import com.example.online_bank.domain.entity.User;
import com.example.online_bank.exception.DeviceNotFoundException;
import com.example.online_bank.repository.TrustedDeviceRepository;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
@Slf4j
public class TrustedDeviceService {
    private final TrustedDeviceRepository trustedDeviceRepository;
    private final VerifiedCodeService verifiedCodeService;

    public void save(TrustedDevice trustedDevice) {
        trustedDeviceRepository.save(trustedDevice);
    }

    public TrustedDevice findByDeviceIdAndUser_email(String email, String deviceId) {
        return trustedDeviceRepository.findByDeviceIdAndUser_email(email, deviceId)
                .orElseThrow(() -> new EntityNotFoundException("TrustedDevice not found"));
    }

    public boolean existsByDeviceIdAndUser_email(String deviceId, String email) {
        return trustedDeviceRepository.existsByDeviceIdAndUser_Email(deviceId, email);
    }

    public void updateUserAgent(String userAgent, TrustedDevice trustedDevice) {
        trustedDevice.setUserAgent(userAgent);
        trustedDeviceRepository.save(trustedDevice);
    }

    /**
     * если не существует пользователя с предоставленным устройством, то выкидываем ошибку
     */
    public void checkExistTrustedDevice(String email, String deviceId, User user) {
        if (!existsByDeviceIdAndUser_email(deviceId, email)) {
            verifiedCodeService.regenerateOtp(new RegenerateOtpDto(email));
            log.info("Была попытка входа с нового устройства. Отправляю отп");
            throw new DeviceNotFoundException("Подтвердите вход с помощью проверочного кода");
        }
    }


    public void deleteByUserAndDeviceId(String deviceId, User user) {
        trustedDeviceRepository.deleteByDeviceIdAndUser(deviceId, user);
    }
}
