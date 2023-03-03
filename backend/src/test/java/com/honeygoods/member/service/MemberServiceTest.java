package com.honeygoods.member.service;

import com.honeygoods.member.domain.Member;
import com.honeygoods.member.dto.MemberSignupRequest;
import com.honeygoods.member.exception.DuplicateEmailException;
import com.honeygoods.member.exception.DuplicateNicknameException;
import com.honeygoods.member.repository.MemberRepository;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;

import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import org.springframework.security.crypto.factory.PasswordEncoderFactories;
import org.springframework.security.crypto.password.PasswordEncoder;

import static org.assertj.core.api.Assertions.assertThatThrownBy;

import static org.mockito.BDDMockito.any;
import static org.mockito.BDDMockito.given;
import static org.mockito.BDDMockito.then;
import static org.mockito.BDDMockito.times;

@ExtendWith(MockitoExtension.class)
class MemberServiceTest {

    @Mock
    private MemberRepository memberRepository;

    @Mock
    private PasswordEncoder passwordEncoder;

    @InjectMocks
    private MemberService memberService;

    @Test
    @DisplayName("회원가입을 한다.")
    void signup_test() {
        MemberSignupRequest request = createMemberSignupRequest();

        String encoded = encryptionPassword(request.getPassword());

        Member member = Member.builder()
                .name(request.getName())
                .nickname(request.getNickname())
                .email(request.getEmail())
                .password(encoded)
                .build();

        given(memberRepository.existsByNickname(any())).willReturn(false);
        given(memberRepository.existsByEmail(any())).willReturn(false);
        given(passwordEncoder.encode(any())).willReturn(encoded);
        given(memberRepository.save(any())).willReturn(member);

        memberService.memberSignup(request);

        then(memberRepository).should(times(1)).existsByNickname(any());
        then(memberRepository).should(times(1)).existsByEmail(any());
        then(passwordEncoder).should(times(1)).encode(any());
        then(memberRepository).should(times(1)).save(any());
    }

    @Test
    @DisplayName("회원가입시 닉네임이 중복되면 예외가 발생한다.")
    void signup_nickname_exception_test() {
        MemberSignupRequest request = createMemberSignupRequest();

        given(memberRepository.existsByNickname(any())).willReturn(true);

        assertThatThrownBy(() -> memberService.memberSignup(request))
                .isInstanceOf(DuplicateNicknameException.class);

        then(memberRepository).should(times(1)).existsByNickname(any());
        then(memberRepository).should(times(0)).existsByEmail(any());
        then(passwordEncoder).should(times(0)).encode(any());
        then(memberRepository).should(times(0)).save(any());
    }

    @Test
    @DisplayName("회원가입시 이메일이 중복되면 예외가 발생한다.")
    void signup_email_exception_test() {
        MemberSignupRequest request = createMemberSignupRequest();

        given(memberRepository.existsByNickname(any())).willReturn(false);
        given(memberRepository.existsByEmail(any())).willReturn(true);

        assertThatThrownBy(() -> memberService.memberSignup(request))
                .isInstanceOf(DuplicateEmailException.class);

        then(memberRepository).should(times(1)).existsByNickname(any());
        then(memberRepository).should(times(1)).existsByEmail(any());
        then(passwordEncoder).should(times(0)).encode(any());
        then(memberRepository).should(times(0)).save(any());
    }

    private MemberSignupRequest createMemberSignupRequest() {
        return new MemberSignupRequest("yoonminsoo", "yoonKun", "yoon@test.com", "123456789");
    }

    private String encryptionPassword(String rawPassword) {
        return PasswordEncoderFactories.createDelegatingPasswordEncoder().encode(rawPassword);
    }

}