package com.globalmed.mes.mes_api.user.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class LockResponse {
    private String message;
    private String retryAfter; // 다시 시도 가능한 시간
}