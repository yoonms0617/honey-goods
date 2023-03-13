package com.honeygoods.common.error.dto;

import com.honeygoods.common.error.exception.ErrorType;

import lombok.Getter;

import java.time.LocalDateTime;

@Getter
public class ErrorResponse {

    private final LocalDateTime timeStamp;

    private final String code;

    private final int status;

    private final String message;

    public ErrorResponse(ErrorType errorType) {
        this.timeStamp = LocalDateTime.now();
        this.code = errorType.getCode();
        this.status = errorType.getStatus();
        this.message = errorType.getMessage();
    }

    public static ErrorResponse of(ErrorType errorType) {
        return new ErrorResponse(errorType);
    }

}
