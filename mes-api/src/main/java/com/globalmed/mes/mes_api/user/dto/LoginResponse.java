package com.globalmed.mes.mes_api.user.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;

@AllArgsConstructor
@Getter
public class LoginResponse {
    private boolean success; // 로그인 성공 여부
    private String token;    // 성공 시 JWT 토큰
    private String message;  // 결과 메시지
}
