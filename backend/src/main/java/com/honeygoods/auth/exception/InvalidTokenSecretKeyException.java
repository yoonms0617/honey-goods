package com.honeygoods.auth.exception;

import org.springframework.security.core.AuthenticationException;

public class InvalidTokenSecretKeyException extends AuthenticationException {

    public InvalidTokenSecretKeyException(String msg) {
        super(msg);
    }

}
