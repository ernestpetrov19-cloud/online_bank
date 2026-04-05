package com.example.online_bank.service;

import com.example.online_bank.domain.dto.RegenerateOtpDto;
import com.example.online_bank.domain.entity.TrustedDevice;
import com.example.online_bank.domain.entity.User;
import com.example.online_bank.exception.DeviceIdIsBlankException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.UUID;

import static com.example.online_bank.enums.SecurityMessage.CONFIRM_LOGIN_MESSAGE;

@Service
@Slf4j
@RequiredArgsConstructor
public class DeviceService {
    private final VerifiedCodeService verifiedCodeService;

    public void checkDeviceBinding(String deviceId, TrustedDevice trustedDevice) {
        if (!trustedDevice.getDeviceId().equals(deviceId)) {
            throw new SecurityException("Подозрительное устройство");
        }
    }

    /**
     * если это новое устройство т.е. deviceId пришел пустым из localStorage,
     * то генерируем его сами и тогда запрашиваем код с почты, фронту в localStorage кладем этот deviceId в качестве ответа,
     */
    public void checkDeviceId(String email, String deviceId, User user) {
        if (deviceId == null) {
            String createdDeviceId = createDeviceId();
            log.info("Устройство не было найдено. Отправляю код для подтверждения");
            verifiedCodeService.regenerateOtp(new RegenerateOtpDto(email));
            throw new DeviceIdIsBlankException(createdDeviceId, CONFIRM_LOGIN_MESSAGE.getValue());
        }
    }

    public String createDeviceId() {
        String checkedDeviceId;
        checkedDeviceId = UUID.randomUUID().toString();
        return checkedDeviceId;
    }

    public String getOrCreateDeviceId(String deviceId) {
        String checkedDeviceId;
        if (deviceId == null || deviceId.isBlank()) {
            checkedDeviceId = createDeviceId();
        } else {
            checkedDeviceId = deviceId;
        }
        return checkedDeviceId;
    }


}
