package com.globalmed.mes.mes_api.user.controller;


import com.wf.captcha.SpecCaptcha;
import com.wf.captcha.base.Captcha;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.io.IOException;

@RequestMapping("/auth")
@RestController
public class CaptchaController {
    @GetMapping("/captcha")
    public void getCaptcha(HttpServletResponse response, HttpSession session) throws IOException {
        // 너비 130, 높이 48, 자리수 4개
        SpecCaptcha captcha = new SpecCaptcha(130, 48, 1);
        captcha.setCharType(Captcha.TYPE_DEFAULT);//영문 숫자 혼합
        session.setAttribute("captcha", captcha.text().toLowerCase());
        response.setHeader("Pragma","No-cache");
        response.setHeader("Cashe-Control","no-cache");
        response.setDateHeader("Expires",0);
        response.setContentType("image/png");
        captcha.out(response.getOutputStream());
    }
}