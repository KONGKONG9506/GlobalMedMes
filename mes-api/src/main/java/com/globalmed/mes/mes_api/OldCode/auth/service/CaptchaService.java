package com.globalmed.mes.mes_api.OldCode.auth.service;

import jakarta.servlet.http.HttpSession;
import org.springframework.stereotype.Service;

@Service
public class CaptchaService {

    private static final String CAPTCHA_SESSION_KEY = "captcha";

    /**
     * 세션에 저장된 captcha 값과 사용자가 입력한 답변을 비교
     */
    public boolean validateCaptcha(HttpSession session, String userAnswer) {
        String sessionCaptcha = (String) session.getAttribute(CAPTCHA_SESSION_KEY);

        // 세션에 captcha가 없거나, 대소문자 무시 비교 시 불일치
        if (sessionCaptcha == null || userAnswer == null) {
            return false;
        }

        return sessionCaptcha.equalsIgnoreCase(userAnswer.trim());
    }

    /**
     * 세션에 captcha 저장
     */
    public void storeCaptcha(HttpSession session, String captchaValue) {
        session.setAttribute(CAPTCHA_SESSION_KEY, captchaValue);
    }

    /**
     * 세션에서 captcha 제거 (사용 후 무효화)
     */
    public void clearCaptcha(HttpSession session) {
        session.removeAttribute(CAPTCHA_SESSION_KEY);
    }
}
