package com.example.online_bank.controller;

import com.example.online_bank.service.BankService;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;

import static org.springframework.boot.test.context.SpringBootTest.WebEnvironment.DEFINED_PORT;

@SpringBootTest(webEnvironment = DEFINED_PORT)
@AutoConfigureMockMvc
@Transactional
public class BankControllerIt {
    private static final String BASE_URL = "http://localhost:8081/api/operation";

    @Autowired
    private BankService bankService;

    void test() {

    }

}
