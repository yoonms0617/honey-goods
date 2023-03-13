package com.honeygoods.member.controller;

import com.fasterxml.jackson.databind.ObjectMapper;

import com.honeygoods.auth.domain.AuthMember;
import com.honeygoods.auth.dto.LoginRequest;
import com.honeygoods.auth.service.JwtService;
import com.honeygoods.common.error.exception.ErrorType;
import com.honeygoods.member.dto.MemberSignupRequest;
import com.honeygoods.member.exception.DuplicateEmailException;
import com.honeygoods.member.exception.DuplicateNicknameException;
import com.honeygoods.member.service.MemberService;

import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.security.Keys;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;

import org.mockito.junit.jupiter.MockitoExtension;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;

import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.http.MediaType;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.factory.PasswordEncoderFactories;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;

import java.nio.charset.StandardCharsets;

import java.util.Date;

import static org.mockito.BDDMockito.any;
import static org.mockito.BDDMockito.given;
import static org.mockito.BDDMockito.willDoNothing;
import static org.mockito.BDDMockito.willThrow;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@ActiveProfiles("test")
@WebMvcTest(
        includeFilters = @ComponentScan.Filter(EnableWebSecurity.class)
)
@ExtendWith(MockitoExtension.class)
class MemberControllerTest {

    private static final Long ID = 1L;

    private static final String NAME = "윤민수";

    private static final String NICKNAME = "yoonms";

    private static final String EMAIL = "yoon@test.com";

    private static final String PASSWORD = "123456789@";

    private static final String ROLE = "ROLE_MEMBER";

    private static final String APPLICATION_JSON = MediaType.APPLICATION_JSON_VALUE;

    private static final String MEMBER_SIGNUP_REQUEST_URI = "/api/member/signup";

    private static final String MEMBER_LOGIN_REQUEST_URI = "/api/member/login";

    @Value("${auth.jwt.secret-key}")
    private String secretKey;

    @Value("${auth.jwt.access-token.expire-length}")
    private long accessTokenExpireInMilliseconds;

    @Value("${auth.jwt.refresh-token.expire-length}")
    private long refreshTokenExpireInMilliseconds;

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockBean
    private UserDetailsService userDetailsService;

    @MockBean
    private JwtService jwtService;

    @MockBean
    private MemberService memberService;

    @Test
    @DisplayName("회원가입 요청을 보낸다. - 성공 200 OK")
    void signup_request() throws Exception {
        willDoNothing().given(memberService).memberSignup(any());

        mockMvc.perform(post(MEMBER_SIGNUP_REQUEST_URI)
                        .contentType(APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(validMemberSignupRequest())))
                .andExpect(status().isOk())
                .andDo(print());
    }

    @Test
    @DisplayName("회원가입 요청을 보낸다. - 잘못된 요청 메소드 405 Method Not Allowed")
    void signup_wrong_method_request() throws Exception {
        ErrorType errorType = ErrorType.METHOD_NOT_SUPPORT;
        willDoNothing().given(memberService).memberSignup(any());

        mockMvc.perform(get(MEMBER_SIGNUP_REQUEST_URI)
                        .contentType(APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(validMemberSignupRequest())))
                .andExpect(status().isMethodNotAllowed())
                .andExpect(jsonPath("$.code").value(errorType.getCode()))
                .andExpect(jsonPath("$.status").value(errorType.getStatus()))
                .andExpect(jsonPath("$.message").value(errorType.getMessage()))
                .andDo(print());
    }

    @Test
    @DisplayName("회원가입 요청을 보낸다. - 닉네임 중복 409 Conflict")
    void signup_nickname_duplicate_request() throws Exception {
        ErrorType errorType = ErrorType.DUPLICATE_NICKNAME;
        willThrow(new DuplicateNicknameException(ErrorType.DUPLICATE_NICKNAME))
                .given(memberService)
                .memberSignup(any());

        mockMvc.perform(post(MEMBER_SIGNUP_REQUEST_URI)
                        .contentType(APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(validMemberSignupRequest())))
                .andExpect(status().isConflict())
                .andExpect(jsonPath("$.code").value(errorType.getCode()))
                .andExpect(jsonPath("$.status").value(errorType.getStatus()))
                .andExpect(jsonPath("$.message").value(errorType.getMessage()))
                .andDo(print());
    }

    @Test
    @DisplayName("회원가입 요청을 보낸다. - 이메일 중복 409 Confilct")
    void signup_email_duplicate_request() throws Exception {
        ErrorType errorType = ErrorType.DUPLICATE_EMAIL;
        willThrow(new DuplicateEmailException(ErrorType.DUPLICATE_EMAIL))
                .given(memberService)
                .memberSignup(any());

        mockMvc.perform(post(MEMBER_SIGNUP_REQUEST_URI)
                        .contentType(APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(validMemberSignupRequest())))
                .andExpect(status().isConflict())
                .andExpect(jsonPath("$.code").value(errorType.getCode()))
                .andExpect(jsonPath("$.status").value(errorType.getStatus()))
                .andExpect(jsonPath("$.message").value(errorType.getMessage()))
                .andDo(print());
    }

    @Test
    @DisplayName("회원가입 요청을 보낸다. - 유효하지 않는 값 400 Bad Request")
    void signup_invalid_value_request() throws Exception {
        ErrorType errorType = ErrorType.INVALID_INPUT_VALUE;
        willDoNothing().given(memberService).memberSignup(any());

        mockMvc.perform(post(MEMBER_SIGNUP_REQUEST_URI)
                        .contentType(APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(invalidMemberSignupRequest())))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.code").value(errorType.getCode()))
                .andExpect(jsonPath("$.status").value(errorType.getStatus()))
                .andExpect(jsonPath("$.message").value(errorType.getMessage()))
                .andDo(print());
    }

    @Test
    @DisplayName("로그인 요청을 보낸다. - 성공 200 OK [Access Token, Refresh Token 반환]")
    void login_request() throws Exception {
        String accessToken = accessToken();
        String refrshToken = refreshToken();
        given(userDetailsService.loadUserByUsername(any())).willReturn(authMember());
        given(jwtService.createAccessToken(any(), any(), any())).willReturn(accessToken);
        given(jwtService.createRefreshToken(any(), any(), any())).willReturn(refrshToken);

        mockMvc.perform(post(MEMBER_LOGIN_REQUEST_URI)
                        .contentType(APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(validLoginRequest()))
                )
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.accessToken").value(accessToken))
                .andExpect(jsonPath("$.refreshToken").value(refrshToken))
                .andDo(print());
    }

    @Test
    @DisplayName("로그인 요청을 보낸다. - 이메일 또는 비밀번호 잘못 입력 400 Bad Request")
    void login_wrong_email_password_request() throws Exception {
        ErrorType errorType = ErrorType.WRONG_EMAIL_PASSWORD;
        given(userDetailsService.loadUserByUsername(any())).willReturn(authMember());

        mockMvc.perform(post(MEMBER_LOGIN_REQUEST_URI)
                        .contentType(APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(inValidLoginRequest()))
                )
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.code").value(errorType.getCode()))
                .andExpect(jsonPath("$.status").value(errorType.getStatus()))
                .andExpect(jsonPath("$.message").value(errorType.getMessage()))
                .andDo(print());
    }

    @Test
    @DisplayName("로그인 요청을 보낸다. - 잘못된 요청 메소드 405 Method Not Allowed")
    void login_wrong_method_request() throws Exception {
        ErrorType errorType = ErrorType.METHOD_NOT_SUPPORT;
        mockMvc.perform(get(MEMBER_LOGIN_REQUEST_URI)
                        .contentType(APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(validLoginRequest()))
                )
                .andExpect(status().isMethodNotAllowed())
                .andExpect(jsonPath("$.code").value(errorType.getCode()))
                .andExpect(jsonPath("$.status").value(errorType.getStatus()))
                .andExpect(jsonPath("$.message").value(errorType.getMessage()))
                .andDo(print());
    }

    private MemberSignupRequest validMemberSignupRequest() {
        return new MemberSignupRequest(NAME, NICKNAME, EMAIL, PASSWORD);
    }

    private MemberSignupRequest invalidMemberSignupRequest() {
        return new MemberSignupRequest("", "", "", "");
    }

    private LoginRequest validLoginRequest() {
        return new LoginRequest(EMAIL, PASSWORD);
    }

    private LoginRequest inValidLoginRequest() {
        return new LoginRequest("", "");
    }

    private AuthMember authMember() {
        String encoded = PasswordEncoderFactories.createDelegatingPasswordEncoder().encode(PASSWORD);
        return new AuthMember(ID, EMAIL, encoded, ROLE);
    }

    private String accessToken() {
        Date iat = new Date();
        Date exp = new Date(iat.getTime() + accessTokenExpireInMilliseconds);
        return Jwts.builder()
                .setSubject(Long.toString(ID))
                .claim("email", EMAIL)
                .claim("role", ROLE)
                .setIssuedAt(iat)
                .setExpiration(exp)
                .signWith(Keys.hmacShaKeyFor(secretKey.getBytes(StandardCharsets.UTF_8)), SignatureAlgorithm.HS256)
                .compact();
    }

    private String refreshToken() {
        Date iat = new Date();
        Date exp = new Date(iat.getTime() + refreshTokenExpireInMilliseconds);
        return Jwts.builder()
                .setSubject(Long.toString(ID))
                .claim("email", EMAIL)
                .claim("role", ROLE)
                .setIssuedAt(iat)
                .setExpiration(exp)
                .signWith(Keys.hmacShaKeyFor(secretKey.getBytes(StandardCharsets.UTF_8)), SignatureAlgorithm.HS256)
                .compact();
    }

}