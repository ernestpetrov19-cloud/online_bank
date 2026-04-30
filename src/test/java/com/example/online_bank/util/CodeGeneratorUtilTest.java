package com.example.online_bank.util;

import com.example.online_bank.enums.CurrencyCode;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

class CodeGeneratorUtilTest {

    @Test
    @DisplayName("Успешная генерация номера счета по старому образцу")
    void generateAccountNumberDeprecated() {
        //arrange подготовка данных
        String accountNumber = CodeGeneratorUtil.generateAccountNumber();
        //act проверяем результат
        assertNotNull(accountNumber);
        assertEquals(6, accountNumber.length());
    }

    @Test
    @DisplayName("Успешная генерация номера по новому образцу")
    void generateAccountNumberByNewFormat() {
        //arrange подготовка данных
        String rubAccountNumber = CodeGeneratorUtil.generateAccountNumber(CurrencyCode.RUB);
        String usdAccountNumber = CodeGeneratorUtil.generateAccountNumber(CurrencyCode.USD);
        String cnyAccountNumber = CodeGeneratorUtil.generateAccountNumber(CurrencyCode.CNY);
        //act проверяем результат
        Assertions.assertTrue(rubAccountNumber.startsWith("810"));
        Assertions.assertTrue(usdAccountNumber.startsWith("840"));
        Assertions.assertTrue(cnyAccountNumber.startsWith("378"));
        Assertions.assertEquals(9, rubAccountNumber.length());
        Assertions.assertEquals(9, usdAccountNumber.length());
        Assertions.assertEquals(9, cnyAccountNumber.length());
    }

    @Test
    @DisplayName("Генерация otp кода")
    void generateVerificationCode() {
        //arrange подготовка данных
        String otp = CodeGeneratorUtil.generateVerificationCode();
        //act проверка результата
        assertNotNull(otp);
        assertEquals(4, otp.length());
    }
}