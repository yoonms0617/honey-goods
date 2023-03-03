package com.honeygoods.member.controller;

import com.fasterxml.jackson.databind.ObjectMapper;

import com.honeygoods.auth.config.SecurityConfig;
import com.honeygoods.common.error.exception.ErrorCode;
import com.honeygoods.member.dto.MemberSignupRequest;
import com.honeygoods.member.exception.DuplicateEmailException;
import com.honeygoods.member.exception.DuplicateNicknameException;
import com.honeygoods.member.service.MemberService;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;

import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.willDoNothing;
import static org.mockito.BDDMockito.willThrow;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest
@Import(SecurityConfig.class)
class MemberControllerTest {

    private static final String APPLICATION_JSON = MediaType.APPLICATION_JSON_VALUE;

    private static final String MEMBER_SIGNUP_REQUEST_URL = "/api/member/signup";

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockBean
    private MemberService memberService;


    @Test
    @DisplayName("회원가입 요청을 보낸다. - 성공 200 OK")
    void signup_request() throws Exception {
        willDoNothing().given(memberService).memberSignup(any());

        MemberSignupRequest request = createValidMemberSignupRequest();
        String jsonData = objectMapper.writeValueAsString(request);
        mockMvc.perform(post(MEMBER_SIGNUP_REQUEST_URL)
                        .contentType(APPLICATION_JSON)
                        .content(jsonData))
                .andExpect(status().isOk())
                .andDo(print());
    }

    @Test
    @DisplayName("회원가입 요청을 보낸다. - 잘못된 요청 메소드 405 Method Not Allowed")
    void signup_wrong_method_request() throws Exception {
        willDoNothing().given(memberService).memberSignup(any());

        MemberSignupRequest request = createValidMemberSignupRequest();
        String jsonData = objectMapper.writeValueAsString(request);
        mockMvc.perform(get(MEMBER_SIGNUP_REQUEST_URL)
                        .contentType(APPLICATION_JSON)
                        .content(jsonData))
                .andExpect(status().isMethodNotAllowed())
                .andExpect(jsonPath("$.code").value("ERR-C-002"))
                .andExpect(jsonPath("$.status").value(405))
                .andExpect(jsonPath("$.message").value("유효하지 않는 요청 메소드입니다."))
                .andDo(print());
    }

    @Test
    @DisplayName("회원가입 요청을 보낸다. - 유효하지 않는 값 400 Bad Request")
    void signup_invalid_value_request() throws Exception {
        willDoNothing().given(memberService).memberSignup(any());

        MemberSignupRequest request = createInvaliMemberSignupRequest();
        String jsonData = objectMapper.writeValueAsString(request);
        mockMvc.perform(post(MEMBER_SIGNUP_REQUEST_URL)
                        .contentType(APPLICATION_JSON)
                        .content(jsonData))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.code").value("ERR-C-001"))
                .andExpect(jsonPath("$.status").value(400))
                .andExpect(jsonPath("$.message").value("유효하지 않는 값입니다."))
                .andDo(print());
    }

    @Test
    @DisplayName("회원가입 요청을 보낸다. - 닉네임 중복 409 Conflict")
    void signup_nickname_duplicate_request() throws Exception {
        willThrow(new DuplicateNicknameException(ErrorCode.DUPLICATE_NICKNAME))
                .given(memberService)
                .memberSignup(any());

        MemberSignupRequest request = createValidMemberSignupRequest();
        String jsonData = objectMapper.writeValueAsString(request);
        mockMvc.perform(post(MEMBER_SIGNUP_REQUEST_URL)
                        .contentType(APPLICATION_JSON)
                        .content(jsonData))
                .andExpect(status().isConflict())
                .andExpect(jsonPath("$.code").value("ERR-M-001"))
                .andExpect(jsonPath("$.status").value(409))
                .andExpect(jsonPath("$.message").value("사용 중인 닉네임입니다."))
                .andDo(print());
    }

    @Test
    @DisplayName("회원가입 요청을 보낸다. - 이메일 중복 409 Confilct")
    void signup_email_duplicate_request() throws Exception {
        willThrow(new DuplicateEmailException(ErrorCode.DUPLICATE_EMAIL))
                .given(memberService)
                .memberSignup(any());

        MemberSignupRequest request = createValidMemberSignupRequest();
        String jsonData = objectMapper.writeValueAsString(request);
        mockMvc.perform(post(MEMBER_SIGNUP_REQUEST_URL)
                        .contentType(APPLICATION_JSON)
                        .content(jsonData))
                .andExpect(status().isConflict())
                .andExpect(jsonPath("$.code").value("ERR-M-002"))
                .andExpect(jsonPath("$.status").value(409))
                .andExpect(jsonPath("$.message").value("사용 중인 이메일입니다."))
                .andDo(print());
    }

    private MemberSignupRequest createValidMemberSignupRequest() {
        return new MemberSignupRequest("yoonminsoo", "yoonKun", "yoon@test.com", "123456789");
    }

    private MemberSignupRequest createInvaliMemberSignupRequest() {
        return new MemberSignupRequest("", "", "yoon@test.com", "");
    }

}