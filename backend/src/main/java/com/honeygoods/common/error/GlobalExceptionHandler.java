package com.honeygoods.common.error;

import com.honeygoods.common.error.dto.ErrorResponse;
import com.honeygoods.common.error.exception.BaseException;
import com.honeygoods.common.error.exception.ErrorType;

import org.springframework.http.ResponseEntity;
import org.springframework.web.HttpRequestMethodNotSupportedException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(BaseException.class)
    public ResponseEntity<ErrorResponse> handleBaseException(BaseException e) {
        ErrorResponse response = ErrorResponse.of(e.getErrorType());
        return ResponseEntity.status(response.getStatus()).body(response);
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ErrorResponse> handleMethodArgumentNotValidException() {
        ErrorResponse response = ErrorResponse.of(ErrorType.INVALID_INPUT_VALUE);
        return ResponseEntity.status(response.getStatus()).body(response);
    }

    @ExceptionHandler(HttpRequestMethodNotSupportedException.class)
    public ResponseEntity<ErrorResponse> handleHttpRequestMethodNotSupportedException() {
        ErrorResponse response = ErrorResponse.of(ErrorType.METHOD_NOT_SUPPORT);
        return ResponseEntity.status(response.getStatus()).body(response);
    }

}
