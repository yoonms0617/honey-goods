package com.honeygoods.auth.handler;

import com.fasterxml.jackson.databind.ObjectMapper;

import com.honeygoods.auth.domain.AuthMember;
import com.honeygoods.auth.dto.LoginResponse;
import com.honeygoods.auth.service.JwtService;

import lombok.RequiredArgsConstructor;

import org.springframework.security.core.Authentication;
import org.springframework.security.web.authentication.AuthenticationSuccessHandler;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import java.io.IOException;

@RequiredArgsConstructor
public class LoginSuccessHandler implements AuthenticationSuccessHandler {

    private final JwtService jwtService;

    private final ObjectMapper objectMapper;

    @Override
    public void onAuthenticationSuccess(HttpServletRequest request, HttpServletResponse response,
                                        Authentication authentication) throws IOException {
        AuthMember principal = (AuthMember) authentication.getPrincipal();
        LoginResponse loginResponse = createLoginResponse(principal);
        objectMapper.writeValue(response.getOutputStream(), loginResponse);
    }

    private LoginResponse createLoginResponse(AuthMember authMember) {
        Long id = authMember.getId();
        String email = authMember.getUsername();
        String role = authMember.getRole();
        String accessToken = jwtService.createAccessToken(id, email, role);
        String refreshToken = jwtService.createRefreshToken(id, email, role);
        return new LoginResponse(accessToken, refreshToken);
    }

}
