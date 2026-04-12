package com.example.online_bank.controller;

import com.example.online_bank.domain.dto.RateRequestDto;
import com.example.online_bank.domain.dto.RateResponseDto;
import com.example.online_bank.domain.entity.ExchangeRate;
import com.example.online_bank.repository.ExchangeCurrencyRepository;
import com.example.online_bank.util.InitializerData;
import io.restassured.RestAssured;
import io.restassured.http.Header;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;

import static com.example.online_bank.enums.CurrencyCode.RUB;
import static com.example.online_bank.enums.CurrencyCode.USD;
import static io.restassured.http.ContentType.JSON;
import static java.math.BigDecimal.valueOf;
import static org.springframework.boot.test.context.SpringBootTest.WebEnvironment.DEFINED_PORT;
import static org.springframework.http.HttpStatus.NOT_FOUND;
import static org.springframework.http.HttpStatus.OK;

@SpringBootTest(webEnvironment = DEFINED_PORT)
@AutoConfigureMockMvc
@Slf4j
public class CurrencyRateControllerIT {
    @Autowired
    private ExchangeCurrencyRepository currencyRepository;
    @Autowired
    private InitializerData initializerData;
    private static final String BASE_URL = "/api/currency";
    private String USER_HEADER_VALUE;

    @SneakyThrows
    @BeforeEach
    void setUp() {
        Thread.sleep(2000);
        RestAssured.port = 8081;
        USER_HEADER_VALUE = "Bearer " + initializerData.initUser();
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
    @DisplayName("Успешно найти курс, с помощью перевернутого курса")
    void successFindInvertedRate() {
        //подготовка данных
        currencyRepository.save(
                ExchangeRate.builder()
                        .id(2L)
                        .baseCurrency(RUB) //создаю курс рубль доллар
                        .targetCurrency(USD)
                        .rate(valueOf(0.01111))
                        .build());

        RateResponseDto expected = new RateResponseDto(USD, RUB, valueOf(90.00900));

        RateResponseDto actual = RestAssured.given()
                .contentType(JSON)
                .log().all()
                .header(new Header("Authorization", USER_HEADER_VALUE))
                .body(new RateRequestDto(USD, RUB)) //в запросе доллар рубль
                .post(BASE_URL + "/find-rate")
                .then()
                .log().all()
                .statusCode(OK.value())
                .extract()
                .as(RateResponseDto.class);

        Assertions.assertEquals(0, expected.rate().compareTo(actual.rate()));
        Assertions.assertEquals(expected.baseCurrency(), actual.baseCurrency());
    }

    @Test
    @DisplayName("Успешно найти курс, хранящийся в базе данных")
    void successFindRate() {
        //подготовка данных
        currencyRepository.save(
                ExchangeRate.builder()
                        .id(2L)
                        .baseCurrency(RUB)
                        .targetCurrency(USD)
                        .rate(valueOf(0.01111))
                        .build());

        RateResponseDto expected = new RateResponseDto(RUB, USD, valueOf(0.01111));

        RateResponseDto actual = RestAssured.given()
                .contentType(JSON)
                .log().all()
                .header(new Header("Authorization", USER_HEADER_VALUE))
                .body(new RateRequestDto(RUB, USD))
                .post(BASE_URL + "/find-rate")
                .then()
                .log().all()
                .statusCode(OK.value())
                .extract()
                .as(RateResponseDto.class);

        Assertions.assertEquals(0, expected.rate().compareTo(actual.rate()));
        Assertions.assertEquals(expected.baseCurrency(), actual.baseCurrency());
    }

    @Test
    void failureFindRate() {
        RestAssured.given()
                .contentType(JSON)
                .log().all()
                .header(new Header("Authorization", USER_HEADER_VALUE))
                .body(new RateRequestDto(USD, RUB))
                .post(BASE_URL + "/find-rate")
                .then()
                .log().all()
                .statusCode(NOT_FOUND.value());
    }
}
