package com.example.online_bank.service;

import com.example.online_bank.exception.UserAgentNotEqualException;
import is.tagomor.woothee.Classifier;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.Map;

import static com.example.online_bank.enums.SecurityMessage.CONFIRM_LOGIN_MESSAGE;
import static is.tagomor.woothee.DataSet.*;

@Slf4j
@Service
public class UserAgentService {

    public void checkUserAgent(String requestedUserAgent, String givenUserAgent) {
        Map<String, String> parsedRequestedUserAgent = parseUserAgent(requestedUserAgent);
        Map<String, String> parsedGivenUserAgent = parseUserAgent(givenUserAgent);

        //проверка операционной системы
        if (!checkEqualsUserAgentsParam(DATASET_KEY_OS, parsedRequestedUserAgent, parsedGivenUserAgent)) {
            log.error("OS not equals");
            throw new UserAgentNotEqualException(CONFIRM_LOGIN_MESSAGE.getValue());
        }

        //Имя браузера
        if (!checkEqualsUserAgentsParam(DATASET_KEY_NAME, parsedRequestedUserAgent, parsedGivenUserAgent)) {
            log.error("Browser name not equals");
            throw new UserAgentNotEqualException(CONFIRM_LOGIN_MESSAGE.getValue());
        }

        //Тип устройства
        if (!checkEqualsUserAgentsParam(DATASET_KEY_CATEGORY, parsedRequestedUserAgent, parsedGivenUserAgent)) {
            log.error("Category not equals");
            throw new UserAgentNotEqualException(CONFIRM_LOGIN_MESSAGE.getValue());
        }
    }

    /**
     * Если, приведенные строки равны, то вернет true
     * если не равны, то false
     */
    public boolean checkBrowserVersion(String requestedUserAgent, String givenUserAgent) {
        Map<String, String> parsedRequestedUserAgent = parseUserAgent(requestedUserAgent);
        Map<String, String> parsedGivenUserAgent = parseUserAgent(givenUserAgent);
        return checkEqualsUserAgentsParam(DATASET_KEY_VERSION, parsedRequestedUserAgent, parsedGivenUserAgent);
    }

    private boolean checkEqualsUserAgentsParam(
            String parameter,
            Map<String, String> parsedGivenUserAgent,
            Map<String, String> parsedRequestedUserAgent
    ) {
        return parsedGivenUserAgent.get(parameter).equals(parsedRequestedUserAgent.get(parameter));
    }

    /**
     * Создает Map из userAgent
     */
    private Map<String, String> parseUserAgent(String userAgent) {
        return Classifier.parse(userAgent);
    }
}
