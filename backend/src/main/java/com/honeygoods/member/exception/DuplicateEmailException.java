package com.honeygoods.member.exception;

import com.honeygoods.common.error.exception.BaseException;
import com.honeygoods.common.error.exception.ErrorCode;

public class DuplicateEmailException extends BaseException {

    public DuplicateEmailException(ErrorCode errorCode) {
        super(errorCode);
    }

}
