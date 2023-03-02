package com.honeygoods.common.error;

import com.honeygoods.common.error.dto.ErrorResponse;
import com.honeygoods.common.error.exception.BaseException;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(BaseException.class)
    public ResponseEntity<ErrorResponse> handleBaseException(BaseException e) {
        ErrorResponse response = ErrorResponse.of(e.getErrorCode());
        return ResponseEntity.status(response.getStatus()).body(response);
    }

}
