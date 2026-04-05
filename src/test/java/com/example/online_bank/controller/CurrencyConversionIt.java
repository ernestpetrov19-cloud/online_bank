package com.example.online_bank.controller;

import com.example.online_bank.domain.dto.ConvertCurrencyRequestDto;
import com.example.online_bank.domain.dto.ConvertCurrencyResponseDto;
import com.example.online_bank.domain.entity.ExchangeRate;
import com.example.online_bank.repository.ExchangeCurrencyRepository;
import com.example.online_bank.service.TokenService;
import com.example.online_bank.service.crud.CrudCurrencyService;
import com.example.online_bank.util.InitializerData;
import io.restassured.RestAssured;
import io.restassured.http.Header;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;

import static com.example.online_bank.enums.CurrencyCode.RUB;
import static com.example.online_bank.enums.CurrencyCode.USD;
import static io.restassured.http.ContentType.JSON;
import static java.math.BigDecimal.valueOf;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.springframework.http.HttpStatus.NOT_FOUND;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.DEFINED_PORT)
@AutoConfigureMockMvc
@Slf4j
public class CurrencyConversionIt {
    @Autowired
    private TokenService tokenService;
    @Autowired
    private CrudCurrencyService crudCurrencyService;
    @Autowired
    private ExchangeCurrencyRepository currencyRepository;
    @Autowired
    private InitializerData<ExchangeRate, ExchangeCurrencyRepository> initializerData;
    private static final String BASE_URL = "/api/currency";
    private String USER_HEADER_VALUE;

    @SneakyThrows
    @BeforeEach
    void setUp() {
        Thread.sleep(2000);
        RestAssured.port = 8081;
        USER_HEADER_VALUE = "Bearer " + initializerData.initUser(currencyRepository);
    }

    @AfterEach
    void cleanUp() {
        int countRowsBeforeDelete = currencyRepository.findAll().size();
        log.info("size of Rate table after delete - {}", countRowsBeforeDelete);
        currencyRepository.deleteAll();
        int countRowsAfterDelete = currencyRepository.findAll().size();
        log.info("size of Rate table after delete - {}", countRowsAfterDelete);
    }

    @Test
    @DisplayName("Конвертировать валюту, но заданный курс не найден и перевернутый курс тоже")
    void failureConvertCurrency_RateWasNotFound() {

        RestAssured.given()
                .contentType(JSON)
                .log().all()
                .header(new Header("Authorization", USER_HEADER_VALUE))
                .body(new ConvertCurrencyRequestDto(USD, RUB, valueOf(50)))
                .post(BASE_URL + "/convert")
                .then()
                .log().all()
                .statusCode(NOT_FOUND.value());
    }

    @Test
    @DisplayName("Успешное конвертирование по найденному курсу")
    void successConvert() {
        ConvertCurrencyRequestDto requestBody = new ConvertCurrencyRequestDto(USD, RUB, valueOf(10));
        ConvertCurrencyResponseDto expected = new ConvertCurrencyResponseDto(USD, RUB, valueOf(900.00), valueOf(10));

        //подготовка данных
        currencyRepository.save(
                ExchangeRate.builder()
                        .id(1L)
                        .rate(valueOf(90))
                        .baseCurrency(USD)
                        .targetCurrency(RUB)
                        .build());

        ConvertCurrencyResponseDto actual = RestAssured.given()
                .contentType(JSON)
                .log().all()
                .header(new Header("Authorization", USER_HEADER_VALUE))
                .body(requestBody)
                .post(BASE_URL + "/convert")
                .then()
                .log().all()
                .statusCode(200)
                .extract()
                .as(ConvertCurrencyResponseDto.class);

        assertNotNull(actual);
        assertEquals(0, expected.targetConvertedAmount().compareTo(actual.targetConvertedAmount()));
        assertEquals(expected.baseCurrency().getCode(), actual.baseCurrency().getCode());
    }

    @Test
    @DisplayName("Успешное конвертирование с помощью инвертирования курса")
    void successConvert_ByInvertedRate() {
        ConvertCurrencyRequestDto requestBody = new ConvertCurrencyRequestDto(USD, RUB, valueOf(10));
        //подготовка данных
        currencyRepository.save(
                ExchangeRate.builder()
                        .id(2L)
                        .baseCurrency(RUB)
                        .targetCurrency(USD)
                        .rate(valueOf(0.01111))
                        .build());
        ConvertCurrencyResponseDto expected = new ConvertCurrencyResponseDto(USD, RUB, valueOf(900.09000), valueOf(10));

        ConvertCurrencyResponseDto actual = RestAssured.given()
                .contentType(JSON)
                .log().all()
                .header(new Header("Authorization", USER_HEADER_VALUE))
                .body(requestBody)
                .post(BASE_URL + "/convert")
                .then()
                .log().all()
                .statusCode(200)
                .extract()
                .as(ConvertCurrencyResponseDto.class);

        assertNotNull(actual);
        assertEquals(0, expected.targetConvertedAmount().compareTo(actual.targetConvertedAmount()));
        assertEquals(expected.baseCurrency().getCode(), actual.baseCurrency().getCode());
    }
}
