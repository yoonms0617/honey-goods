package com.honeygoods.member.exception;

import com.honeygoods.common.error.exception.BaseException;
import com.honeygoods.common.error.exception.ErrorCode;

public class DuplicateNicknameException extends BaseException {

    public DuplicateNicknameException(ErrorCode errorCode) {
        super(errorCode);
    }

}
