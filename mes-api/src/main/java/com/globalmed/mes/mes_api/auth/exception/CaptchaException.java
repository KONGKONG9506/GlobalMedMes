package com.globalmed.mes.mes_api.auth.exception;

import org.springframework.http.HttpStatus;

import java.util.Map;

public class CaptchaException extends RuntimeException {
    private final String code;
    private final HttpStatus status;
    private final Map<String,Object> details;

    public CaptchaException(String code, String message, HttpStatus status, Map<String,Object> details) {
        super(message);
        this.code = code;
        this.status = status;
        this.details = details;
    }

    public String getCode() { return code; }
    public HttpStatus getStatus() { return status; }
    public Map<String,Object> getDetails() { return details; }
}
