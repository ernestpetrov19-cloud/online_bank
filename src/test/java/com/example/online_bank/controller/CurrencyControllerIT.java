package com.example.online_bank.controller;

import com.example.online_bank.domain.dto.*;
import com.example.online_bank.domain.entity.ExchangeRate;
import com.example.online_bank.repository.ExchangeCurrencyRepository;
import com.example.online_bank.service.crud.CrudCurrencyService;
import com.example.online_bank.util.InitializerData;
import io.restassured.RestAssured;
import io.restassured.http.Header;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;

import java.math.BigDecimal;
import java.util.Map;
import java.util.Optional;

import static com.example.online_bank.enums.CurrencyCode.RUB;
import static com.example.online_bank.enums.CurrencyCode.USD;
import static io.restassured.http.ContentType.JSON;
import static java.math.BigDecimal.ZERO;
import static java.math.BigDecimal.valueOf;
import static org.junit.jupiter.api.Assertions.*;
import static org.springframework.http.HttpStatus.*;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.DEFINED_PORT)
@AutoConfigureMockMvc
@Slf4j
class CurrencyControllerIT {
    @Autowired
    private CrudCurrencyService crudCurrencyService;
    @Autowired
    private ExchangeCurrencyRepository currencyRepository;
    @Autowired
    private InitializerData<ExchangeRate, ExchangeCurrencyRepository> initializerData;
    private static final String BASE_URL = "/api/currency";
    private String userAccessToken;
    private String adminAccessToken;
    private Map<String, Object> refreshToken;
    private String USER_HEADER_VALUE;
    private String ADMIN_HEADER_VALUE;
    private CreateExchangeRateDto createRateDto;

    @SneakyThrows
    @BeforeEach
    void setUp() {
        userAccessToken = initializerData.initUser(currencyRepository);
        adminAccessToken = initializerData.initAdmin(currencyRepository);
        Thread.sleep(2000);
        RestAssured.port = 8081;
        USER_HEADER_VALUE = "Bearer " + userAccessToken;
        ADMIN_HEADER_VALUE = "Bearer " + adminAccessToken;
        createRateDto = new CreateExchangeRateDto(USD, RUB, valueOf(90));
    }

    void cleanUp() {
        currencyRepository.deleteAll();
    }

    @Test
    @DisplayName("Успешное создание курса через администратора.")
    @Disabled
    void successCreateExchangeRate() {
        RateResponseDto expected = new RateResponseDto(USD, RUB, valueOf(90));
        RateResponseDto actual = RestAssured.given()
                .contentType(JSON)
                .log().all()
                .header("Authorization", ADMIN_HEADER_VALUE)
                .body(createRateDto)
                .post(BASE_URL + "/create")
                .then()
                .log().all()
                .statusCode(CREATED.value())
                .extract()
                .as(RateResponseDto.class);

        assertNotNull(actual);
        assertEquals(expected.baseCurrency(), actual.baseCurrency());
        assertEquals(expected.targetCurrency(), actual.targetCurrency());
        assertEquals(expected.rate(), actual.rate());
    }

    @Test
    @DisplayName("Неудачное создание курса валют для администратора. Ставка курса ровна нулю.")
    @Disabled
    void failureCreateExchangeRate() {
        RestAssured.given()
                .contentType(JSON)
                .log().all()
                .header("Authorization", ADMIN_HEADER_VALUE)
                .body(new CreateExchangeRateDto(USD, RUB, ZERO))
                .post(BASE_URL + "/create")
                .then()
                .log().all()
                .statusCode(BAD_REQUEST.value());
    }

    @Test
    @DisplayName("Неудачное создание ставки через пользователя")
    @Disabled
    void failureCreateRateByUser() {
        RestAssured.given()
                .contentType(JSON)
                .log().all()
                .header("Authorization", USER_HEADER_VALUE)
                .body(createRateDto)
                .post(BASE_URL + "/create")
                .then()
                .log().all()
                .statusCode(FORBIDDEN.value());
    }

    @Test
    @DisplayName("Успешно найти курс, который хранится в бд")
    @Disabled
    void successFindRate_ByUser() {
        crudCurrencyService.create(USD, RUB, valueOf(90));
        RestAssured.given()
                .contentType(JSON)
                .log().all()
                .header(new Header("Authorization", USER_HEADER_VALUE))
                .body(new RateRequestDto(USD, RUB))
                .post(BASE_URL + "/find-rate")
                .then()
                .log().all()
                .statusCode(OK.value());
    }

    @Test
    @Disabled
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
        //подготовка данных
        crudCurrencyService.create(USD, RUB, valueOf(90));
        ConvertCurrencyRequestDto requestBody = new ConvertCurrencyRequestDto(USD, RUB, valueOf(10));
        ConvertCurrencyResponseDto expected = new ConvertCurrencyResponseDto(USD, RUB, valueOf(900.00), valueOf(90));

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
        assertTrue(expected.targetConvertedAmount().compareTo(actual.targetConvertedAmount()) == 0);
    }

    @Test
    @DisplayName("Успешное конвертирование с помощью инвертирования курса")
    void successConvert_ByInvertedRate() {
        ConvertCurrencyRequestDto requestBody = new ConvertCurrencyRequestDto(USD, RUB, valueOf(10));
        crudCurrencyService.create(RUB, USD, valueOf(0.01111));
        ConvertCurrencyResponseDto expected = new ConvertCurrencyResponseDto(USD, RUB, valueOf(900.00), valueOf(90));
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
        assertEquals(expected.targetConvertedAmount(), actual.targetConvertedAmount());
    }

    @Test
    void successSaveDataInDataBase() {
        crudCurrencyService.create(USD, RUB, valueOf(90));
        Optional<BigDecimal> rate = currencyRepository.findRateByBaseAndTargetCurrency(USD, RUB);
        assertTrue(rate.isPresent());
        assertEquals(0, rate.get().compareTo(valueOf(90)));
        log.info("rate - {}", rate.get());
    }

    @Test
    void failureSaveDataInDataBase() {
        Optional<BigDecimal> rate = currencyRepository.findRateByBaseAndTargetCurrency(USD, RUB);
        assertFalse(rate.isPresent());
    }
}