package com.honeygoods.common.error.dto;

import com.honeygoods.common.error.exception.ErrorCode;

import lombok.Getter;

import java.time.LocalDateTime;

@Getter
public class ErrorResponse {

    private final LocalDateTime timeStamp;

    private final String code;

    private final int status;

    private final String message;

    public ErrorResponse(ErrorCode errorCode) {
        this.timeStamp = LocalDateTime.now();
        this.code = errorCode.getCode();
        this.status = errorCode.getStatus();
        this.message = errorCode.getMessage();
    }

    public static ErrorResponse of(ErrorCode errorCode) {
        return new ErrorResponse(errorCode);
    }

}
