package com.example.online_bank.controller;

import com.example.online_bank.domain.dto.CreateExchangeRateDto;
import com.example.online_bank.domain.dto.RateResponseDto;
import com.example.online_bank.domain.entity.ExchangeRate;
import com.example.online_bank.repository.ExchangeCurrencyRepository;
import com.example.online_bank.service.crud.CrudCurrencyService;
import com.example.online_bank.util.InitializerData;
import io.restassured.RestAssured;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;

import static com.example.online_bank.enums.CurrencyCode.RUB;
import static com.example.online_bank.enums.CurrencyCode.USD;
import static io.restassured.http.ContentType.JSON;
import static java.math.BigDecimal.ZERO;
import static java.math.BigDecimal.valueOf;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.springframework.boot.test.context.SpringBootTest.WebEnvironment.DEFINED_PORT;
import static org.springframework.http.HttpStatus.*;

@SpringBootTest(webEnvironment = DEFINED_PORT)
@AutoConfigureMockMvc
@Slf4j
public class CurrencyControllerCrudIt {
    @Autowired
    private CrudCurrencyService crudCurrencyService;
    @Autowired
    private ExchangeCurrencyRepository currencyRepository;
    @Autowired
    private InitializerData<ExchangeRate, ExchangeCurrencyRepository> initializerData;
    private static final String BASE_URL = "/api/currency";
    private String USER_HEADER_VALUE;
    private String ADMIN_HEADER_VALUE;
    private CreateExchangeRateDto createRateDto;

    @SneakyThrows
    @BeforeEach
    void setUp() {
        Thread.sleep(2000);
        RestAssured.port = 8081;
        USER_HEADER_VALUE = initializerData.initUser(currencyRepository);
        ADMIN_HEADER_VALUE = initializerData.initAdmin(currencyRepository);
        createRateDto = new CreateExchangeRateDto(USD, RUB, valueOf(90));
    }

    @Test
    @DisplayName("Успешное создание курса через администратора.")
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
    @DisplayName("Неудачное создание курса валют через пользователя")
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

}
