package com.example.online_bank.controller;

import com.example.online_bank.domain.dto.RegistrationDtoRequest;
import com.example.online_bank.domain.entity.User;
import com.example.online_bank.repository.UserRepository;
import com.example.online_bank.util.InitializerData;
import io.restassured.RestAssured;
import io.restassured.http.Header;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.HttpStatus;

import static io.restassured.http.ContentType.JSON;
import static org.springframework.boot.test.context.SpringBootTest.WebEnvironment.DEFINED_PORT;

@SpringBootTest(webEnvironment = DEFINED_PORT)
@AutoConfigureMockMvc
@Slf4j
class RegistrationControllerIT {
    @Autowired
    private InitializerData initializerData;
    @Autowired
    private UserRepository userRepository;

    private static RegistrationDtoRequest requestBody;

    private static String ADMIN_HEADER_VALUE;
    private static String USER_HEADER_VALUE;
    private final String url = "/api/sign-up";

    @SneakyThrows
    @BeforeEach
    void initTokens() {
        Thread.sleep(2000);
        RestAssured.port = 8081;
        ADMIN_HEADER_VALUE = "Bearer " + initializerData.initAdmin();
        USER_HEADER_VALUE = "Bearer " + initializerData.initUser();
        requestBody = new RegistrationDtoRequest(
                "testName",
                "testSurname",
                "testPatronymic",
                "79999999999",
                "1234",
                "testEmail@gmail.com"

        );
    }

    @AfterEach
    void cleanUp() {
        userRepository.deleteAll();
    }

    @Nested
    @DisplayName("Сценарии регистрации пользователя")
    class UserScenario {
        @Test
        void successUserSignUp() {
            RestAssured.given()
                    .contentType(JSON)
                    .log().all()
                    .body(requestBody)
                    .post(url)
                    .then()
                    .log().all()
                    .statusCode(HttpStatus.CREATED.value());

        }

        @Test
        void failureUserSignUp_UserWithProvidedEmailAlreadyExists() {
            // 1. Подготовка среды
            User userMock = User.builder().email(requestBody.email()).build();
            userRepository.save(userMock);

            // 2. Assert
            RestAssured.given()
                    .contentType(JSON)
                    .log().all()
                    .body(requestBody)
                    .post(url)
                    .then()
                    .log().all()
                    .statusCode(HttpStatus.CONFLICT.value());
        }

        @Test
        void failureUserSignUp_UserWithProvidedPhoneAlreadyExists() {
            // 1. Подготовка среды
            User userMock = User.builder().phoneNumber(requestBody.phone()).build();
            userRepository.save(userMock);

            // 2. Assert
            RestAssured.given()
                    .contentType(JSON)
                    .log().all()
                    .body(requestBody)
                    .post(url)
                    .then()
                    .log().all()
                    .statusCode(HttpStatus.CONFLICT.value());
        }

    }

    @Nested
    @DisplayName("Сценарии регистрации администратора")
    class AdminScenario {
        @Test
        void successSignUpAdmin() {
            RestAssured.given()
                    .contentType(JSON)
                    .log().all()
                    .header(new Header("Authorization", ADMIN_HEADER_VALUE))
                    .body(requestBody)
                    .post(url + "/admin")
                    .then()
                    .log().all()
                    .statusCode(HttpStatus.CREATED.value());
        }

        @Test
        void failureSignUpAdmin_EmailAlreadyExists() {
            User userMock = User.builder().email(requestBody.email()).build();
            userRepository.save(userMock);

            RestAssured.given()
                    .contentType(JSON)
                    .log().all()
                    .header(new Header("Authorization", ADMIN_HEADER_VALUE))
                    .body(requestBody)
                    .post(url + "/admin")
                    .then()
                    .log().all()
                    .statusCode(HttpStatus.CONFLICT.value());
        }

        @Test
        void failureSignUpAdmin_PhoneAlreadyExists() {
            User userMock = User.builder().phoneNumber(requestBody.phone()).build();
            userRepository.save(userMock);

            RestAssured.given()
                    .contentType(JSON)
                    .log().all()
                    .header(new Header("Authorization", ADMIN_HEADER_VALUE))
                    .body(requestBody)
                    .post(url + "/admin")
                    .then()
                    .log().all()
                    .statusCode(HttpStatus.CONFLICT.value());
        }

        @Test
        void failureRegistration_UserRegistratedWithPhoneAndEmail() {
            User userMock = User.builder()
                    .email(requestBody.email())
                    .phoneNumber(requestBody.phone())
                    .build();

            userRepository.save(userMock);

            RestAssured.given()
                    .contentType(JSON)
                    .log().all()
                    .header(new Header("Authorization", ADMIN_HEADER_VALUE))
                    .body(requestBody)
                    .post(url + "/admin")
                    .then()
                    .log().all()
                    .statusCode(HttpStatus.CONFLICT.value());
        }

        @Test
        void failureRegistration_AccessDeniedByUserToken() {
            RestAssured.given()
                    .contentType(JSON)
                    .log().all()
                    .header(new Header("Authorization", USER_HEADER_VALUE))
                    .body(requestBody)
                    .post(url + "/admin")
                    .then()
                    .log().all()
                    .statusCode(HttpStatus.FORBIDDEN.value());
        }

        @Test
        void failureRegistration_TokenNotProvided() {
            RestAssured.given()
                    .contentType(JSON)
                    .log().all()
                    .body(requestBody)
                    .post(url + "/admin")
                    .then()
                    .log().all()
                    .statusCode(HttpStatus.UNAUTHORIZED.value());
        }
    }


}