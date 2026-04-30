package com.example.online_bank.domain.dto;

import java.math.BigDecimal;

/**
 * DTO для перевода средств между клиентами.
 * Имя банка указывается у каждого клиента отдельно
 *
 * @param senderInfo             Информация об отправителе (Имя, фамилия,
 *                               отчество(при наличии), номер счета)
 * @param recipientInfo          Информация об получателе (Имя, фамилия,
 *                               отчество(при наличии), номер счета, имя банка)
 * @param recipientRequestAmount Количество денег, которое запросил получатель,
 *                               в его же валюте
 * @param description            Описание к операции
 */
public record TransferDto(
        SenderInfo senderInfo,
        RecipientInfo recipientInfo,
        BigDecimal recipientRequestAmount,
        String description
) {
}
