package com.globalmed.mes.mes_api.auth.dto;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class LoginRequest {
    private String username;       // userName → username
    private String password;       // 평문 비밀번호
    private String captchaAnswer;
}
