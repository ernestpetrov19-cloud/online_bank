package com.example.online_bank.service;

import com.example.online_bank.domain.dto.FinanceOperationDto;
import com.example.online_bank.domain.dto.OperationInfoDto;
import com.example.online_bank.domain.dto.PayDtoRequest;
import com.example.online_bank.domain.entity.User;
import com.example.online_bank.domain.event.UpdateUserStatEvent;
import com.example.online_bank.enums.CurrencyCode;
import com.example.online_bank.repository.UserRepository;
import jakarta.persistence.EntityNotFoundException;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class PayBankPartnerService {
    private final BankService bankService;
    private final ApplicationEventPublisher applicationEventPublisher;
    private final BankPartnerService bankPartnerService;
    private final UserRepository userRepository;

    @Transactional
    public OperationInfoDto pay(PayDtoRequest payDtoRequest, UUID userUuid) {
        CurrencyCode partnerAccountCurrencyCode = bankPartnerService.getAccountCurrencyCode();

        User user = userRepository.findByUuid(userUuid).orElseThrow(
                () -> new EntityNotFoundException("User not found")
        );

        //снимаем деньги со счета отправителя
        String description = createDescription(payDtoRequest);
        String userAccountNumber = payDtoRequest.senderInfo().accountNumberFrom();
        OperationInfoDto senderOperationResponse = bankService.makePayment(
                new FinanceOperationDto(
                        userAccountNumber,
                        payDtoRequest.serviceRequestAmount(),
                        description,
                        partnerAccountCurrencyCode)
        );

        // обновление статистики пользователя
        UpdateUserStatEvent updateUserStatEvent = new UpdateUserStatEvent(
                user,
                payDtoRequest.category(),
                payDtoRequest.serviceRequestAmount(),
                LocalDate.now(),
                userAccountNumber
        );
        applicationEventPublisher.publishEvent(updateUserStatEvent);

        //пополняем счет партнера/сервиса
        FinanceOperationDto serviceDto = createRecipientDto(payDtoRequest, partnerAccountCurrencyCode, description);
        bankService.makeDeposit(serviceDto);
        return senderOperationResponse;
    }

    private String createDescription(PayDtoRequest payDtoRequest) {
        return "Оплата сервиса %s".formatted(payDtoRequest.serviceInfo().partnerName());
    }

    private FinanceOperationDto createRecipientDto(
            PayDtoRequest payDtoRequest,
            CurrencyCode recipientCurrencyCode,
            String description
    ) {
        String partnerAccountNumber = bankPartnerService.getAccountNumber(payDtoRequest.serviceInfo().partnerName());
        return new FinanceOperationDto(
                partnerAccountNumber,
                payDtoRequest.serviceRequestAmount(),
                description,
                recipientCurrencyCode);
    }
}
