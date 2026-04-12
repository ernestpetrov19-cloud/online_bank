package com.example.online_bank.domain.event;

/**
 * @param email email
 * @param code  otp
 */
public record SendOtpEvent(
        String email,
        String code,
        String bodyText
) {
}
