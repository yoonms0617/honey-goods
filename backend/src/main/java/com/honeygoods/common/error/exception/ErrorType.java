package com.honeygoods.common.error.exception;

import lombok.Getter;

import java.util.Arrays;

@Getter
public enum ErrorType {

    UNSUPPORTED_ERROR_TYPE("ERR-C-000", 400, "지원하지 않는 에러유형 입니다."),
    INVALID_INPUT_VALUE("ERR-C-001", 400, "유효하지 않는 입력 값입니다."),
    METHOD_NOT_SUPPORT("ERR-C-002", 405, "유효하지 않는 요청 메소드입니다."),

    INVALID_TOKEN("ERR-AUTH-001", 401, "유효하지 않는 토큰입니다."),
    EXPIRED_TOKEN("ERR-AUTH-002", 401, "토큰이 만료되었습니다."),
    INVALID_TOKEN_SECRET_KEY("ERR-AUTH-003", 401, "비밀키가 유효하지 않습니다."),

    DUPLICATE_NICKNAME("ERR-M-001", 409, "사용 중인 닉네임입니다."),
    DUPLICATE_EMAIL("ERR-M-002", 409, "사용 중인 이메일입니다."),
    WRONG_EMAIL_PASSWORD("ERR-M-003", 400, "아이디(이메일) 또는 비밀번호를 잘못 입력했습니다.");

    private final String code;

    private final int status;

    private final String message;

    ErrorType(String code, int status, String message) {
        this.code = code;
        this.status = status;
        this.message = message;
    }

    public static ErrorType findByCode(String code) {
        return Arrays.stream(ErrorType.values())
                .filter(errorType -> errorType.getCode().equals(code))
                .findFirst()
                .orElse(UNSUPPORTED_ERROR_TYPE);
    }

}
