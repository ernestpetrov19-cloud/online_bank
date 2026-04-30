package com.example.online_bank.service;

import com.example.online_bank.repository.VerificationCodeRepository;
import com.example.online_bank.service.domain.VerificationCodeService;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.springframework.test.context.junit.jupiter.SpringExtension;

@ExtendWith(SpringExtension.class)
class VerificationCodeServiceTest {
    @Mock
    private VerificationCodeRepository verificationCodeRepository;
    @InjectMocks
    private VerificationCodeService verificationCodeService;

}