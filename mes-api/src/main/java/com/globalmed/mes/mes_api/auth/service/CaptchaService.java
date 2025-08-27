package com.globalmed.mes.mes_api.auth.service;


import com.globalmed.mes.mes_api.auth.exception.CaptchaException;
import jakarta.servlet.http.HttpSession;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

import java.util.Map;

@Service
public class CaptchaService {

    private static final String CAPTCHA_SESSION_KEY = "captcha";

    public void validateCaptcha(HttpSession session, String userAnswer) {
        String sessionCaptcha = (String) session.getAttribute(CAPTCHA_SESSION_KEY);

        if (sessionCaptcha == null) {
            throw new CaptchaException(
                    "CAPTCHA_NOT_FOUND",
                    "캡챠가 생성되지 않았습니다.",
                    HttpStatus.INTERNAL_SERVER_ERROR,
                    null
            );
        }

        if (userAnswer == null || !sessionCaptcha.equalsIgnoreCase(userAnswer.trim())) {
            throw new CaptchaException(
                    "CAPTCHA_INVALID",
                    "잘못된 캡챠 입력값입니다.",
                    HttpStatus.BAD_REQUEST,
                    Map.of("input", userAnswer)
            );
        }

        // ✅ 정상 통과 시 세션에서 삭제
        clearCaptcha(session);
    }

    public void storeCaptcha(HttpSession session, String captchaValue) {
        session.setAttribute(CAPTCHA_SESSION_KEY, captchaValue);
    }

    public void clearCaptcha(HttpSession session) {
        session.removeAttribute(CAPTCHA_SESSION_KEY);
    }
}
