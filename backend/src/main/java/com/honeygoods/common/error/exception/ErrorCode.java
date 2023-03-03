package com.honeygoods.common.error.exception;

import lombok.Getter;

@Getter
public enum ErrorCode {

    INVALID_INPUT_VALUE("ERR-C-001", 400, "유효하지 않는 값입니다."),
    METHOD_NOT_SUPPORT("ERR-C-002", 405, "유효하지 않는 요청 메소드입니다."),

    DUPLICATE_NICKNAME("ERR-M-001", 409, "사용 중인 닉네임입니다."),
    DUPLICATE_EMAIL("ERR-M-002", 409, "사용 중인 이메일입니다.");

    private final String code;

    private final int status;

    private final String message;

    ErrorCode(String code, int status, String message) {
        this.code = code;
        this.status = status;
        this.message = message;
    }

}
