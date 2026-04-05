package com.example.online_bank.controller;

import com.example.online_bank.domain.dto.RegistrationDto;
import io.restassured.RestAssured;
import jakarta.transaction.Transactional;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;

import static io.restassured.http.ContentType.JSON;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.DEFINED_PORT)
@AutoConfigureMockMvc
@Transactional
public class RegistrationControllerTestIT {
    private static final String BASE_URL = "api/sign-up";
    private RegistrationDto registrationDto;

    @BeforeEach
    void setUp() {
        registrationDto = new RegistrationDto("Amir", "Gilmanov", "Doe", "+79608052797", "1234", "myEmail.com");
    }

    @Test
    void successRegistration() {
        RestAssured.given()
                .contentType(JSON)
                .log().all()
                .body(registrationDto)
                .post()
                .then()
                .log().all()
                .statusCode(201);
    }
}
