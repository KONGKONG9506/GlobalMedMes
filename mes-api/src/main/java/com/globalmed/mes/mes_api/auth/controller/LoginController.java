package com.globalmed.mes.mes_api.auth.controller;

import com.globalmed.mes.mes_api.auth.domain.UserEntity;
import com.globalmed.mes.mes_api.auth.dto.LockResponse;
import com.globalmed.mes.mes_api.auth.dto.LoginRequest;
import com.globalmed.mes.mes_api.auth.dto.LoginResponse;
import com.globalmed.mes.mes_api.auth.service.CaptchaService;
import com.globalmed.mes.mes_api.auth.service.JwtProvider;
import com.globalmed.mes.mes_api.auth.service.UserService;
import com.globalmed.mes.mes_api.auth.userrole.service.UserRoleService;
import jakarta.servlet.http.HttpSession;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.Optional;

@Slf4j
@RequestMapping("/auth")
@AllArgsConstructor
@RestController
public class LoginController {
    private final UserService userService;
    private final JwtProvider jwtProvider;
    private final BCryptPasswordEncoder encoder;
    private final UserRoleService userRoleService;
    private final CaptchaService captchaService; //

    @PostMapping("/login")
    public ResponseEntity<?> login(@RequestBody LoginRequest request, HttpSession session) {
//        CAPTCHA 임시 비활성화 하려면 해당 부분 주석 처리
//        if (!captchaService.validateCaptcha(session, request.getCaptchaAnswer())) {
//            log.warn("CAPTCHA mismatch for username='{}'", request.getUsername());
//            return ResponseEntity.badRequest()
//                    .body(new LoginResponse(false, null, "captcha가 틀렸습니다."));
//        }
//        여기까지
        Optional<UserEntity> userOpt = userService.findByUsername(request.getUsername());

        if (userOpt.isEmpty()) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                    .body(new LoginResponse(false, null, "아이디 또는 비밀번호가 다릅니다."));
        }
            UserEntity user = userOpt.get();
        if (userService.isAccountLocked(user)) {
            return ResponseEntity.status(HttpStatus.LOCKED)
                    .body(new LockResponse("계정이 잠겨있습니다.", user.getLockedUntil().toString()));
        }

        // 비밀번호 검증
        if (encoder.matches(request.getPassword(), user.getPasswordHash())) {
            // 성공: 실패횟수 초기화
            userService.resetFailedAttempts(user);
            List<String> roles = userRoleService.getRolesByUserId(user.getUserId());
            // JWT 발급
            String token = jwtProvider.createToken(user.getUsername(), roles);

            // 세션에 사용자 정보 저장 (2시간 유지)
            session.setAttribute("USER", user);
            session.setMaxInactiveInterval(60 * 60 * 2);
            captchaService.clearCaptcha(session);
            return ResponseEntity.ok(new LoginResponse(true, token, "Login success"));
        } else {
            // 실패: 실패횟수 증가 및 잠금 처리
            userService.increaseFailedAttempts(user);

            int failCount = user.getFailedLoginCount();
            int maxAttempts = UserService.MAX_FAILED_ATTEMPTS;
            int remaining = maxAttempts - failCount;

            log.error("Login failed for username='{}': failCount={}, remaining={}",
                    request.getUsername(), failCount, remaining);

            if (remaining > 0) {
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                        .body(new LoginResponse(false, null,
                                "아이디 또는 비밀번호가 다릅니다. (" + failCount + "회 실패, " +
                                        remaining + "회 더 실패하면 계정이 잠깁니다)"));
            } else {
                return ResponseEntity.status(HttpStatus.LOCKED)
                        .body(new LockResponse("비밀번호를 " + maxAttempts + "회 틀리셔서 계정이 잠겼습니다.",
                                user.getLockedUntil().toString()));
            }

        }
    }
}
