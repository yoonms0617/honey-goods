package com.honeygoods.common.error.exception;

import lombok.Getter;

@Getter
public enum ErrorCode {

    ;

    private final String code;

    private final int status;

    private final String message;

    ErrorCode(String code, int status, String message) {
        this.code = code;
        this.status = status;
        this.message = message;
    }

}
