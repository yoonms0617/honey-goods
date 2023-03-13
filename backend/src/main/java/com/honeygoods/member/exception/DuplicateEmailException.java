package com.honeygoods.member.exception;

import com.honeygoods.common.error.exception.BaseException;
import com.honeygoods.common.error.exception.ErrorType;

public class DuplicateEmailException extends BaseException {

    public DuplicateEmailException(ErrorType errorType) {
        super(errorType);
    }

}
