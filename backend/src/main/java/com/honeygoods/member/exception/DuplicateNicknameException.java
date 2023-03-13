package com.honeygoods.member.exception;

import com.honeygoods.common.error.exception.BaseException;
import com.honeygoods.common.error.exception.ErrorType;

public class DuplicateNicknameException extends BaseException {

    public DuplicateNicknameException(ErrorType errorType) {
        super(errorType);
    }

}
