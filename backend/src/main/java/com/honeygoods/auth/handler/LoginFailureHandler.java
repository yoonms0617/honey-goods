package com.honeygoods.auth.handler;

import com.fasterxml.jackson.databind.ObjectMapper;

import com.honeygoods.common.error.dto.ErrorResponse;
import com.honeygoods.common.error.exception.ErrorType;

import lombok.RequiredArgsConstructor;

import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.authentication.AuthenticationFailureHandler;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import java.io.IOException;

@RequiredArgsConstructor
public class LoginFailureHandler implements AuthenticationFailureHandler {

    private final ObjectMapper objectMapper;

    @Override
    public void onAuthenticationFailure(HttpServletRequest request, HttpServletResponse response,
                                        AuthenticationException exception) throws IOException {
        String code = exception.getMessage();
        ErrorType errorType = ErrorType.findByCode(code);
        ErrorResponse errorResponse = ErrorResponse.of(errorType);
        response.setStatus(errorResponse.getStatus());
        objectMapper.writeValue(response.getWriter(), errorResponse);
    }

}
