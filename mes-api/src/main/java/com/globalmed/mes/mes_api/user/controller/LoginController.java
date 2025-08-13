package com.globalmed.mes.mes_api.user.controller;

import com.globalmed.mes.mes_api.user.domain.UserEntity;
import com.globalmed.mes.mes_api.user.dto.LoginRequest;
import com.globalmed.mes.mes_api.user.dto.LoginResponse;
import com.globalmed.mes.mes_api.user.service.JwtProvider;
import com.globalmed.mes.mes_api.user.service.UserService;
import jakarta.servlet.http.HttpSession;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Optional;

@Slf4j
@RequestMapping("/auth")
@AllArgsConstructor
@RestController
public class LoginController {
    private final UserService userService;
    private final JwtProvider jwtProvider;
    private final BCryptPasswordEncoder encoder;

    @PostMapping("/login")
    public LoginResponse login(@RequestBody LoginRequest request, HttpSession session) {
        String sessionCaptcha = (String) session.getAttribute("captcha");
        if (sessionCaptcha == null || !sessionCaptcha.equalsIgnoreCase(request.getCaptchaAnswer())) {
            log.error("CAPTCHA mismatch: session='{}', request='{}'", sessionCaptcha, request.getCaptchaAnswer());
            return new LoginResponse(false, null, "captcha가 틀렸습니다");
        }

        Optional<UserEntity> userOpt = userService.findByUsername(request.getUsername());

        if (userOpt.isPresent() && encoder.matches(request.getPassword(), userOpt.get().getPasswordHash())) {
            String token = jwtProvider.createToken(userOpt.get().getUsername());
            return new LoginResponse(true, token, "Login success");
        } else {
            log.error("Login failed for username='{}': user found={}, password match={}",
                    request.getUsername(), userOpt.isPresent(), userOpt.isPresent() ? encoder.matches(request.getPassword(), userOpt.get().getPasswordHash()) : false);
            return new LoginResponse(false, null, "아이디 또는 비밀번호가 다릅니다.");
        }
    }
}
