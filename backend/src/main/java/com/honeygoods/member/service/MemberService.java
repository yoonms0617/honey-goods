package com.honeygoods.member.service;

import com.honeygoods.common.error.exception.ErrorType;
import com.honeygoods.member.domain.Member;
import com.honeygoods.member.dto.MemberSignupRequest;
import com.honeygoods.member.exception.DuplicateEmailException;
import com.honeygoods.member.exception.DuplicateNicknameException;
import com.honeygoods.member.repository.MemberRepository;

import lombok.RequiredArgsConstructor;

import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class MemberService {

    private final MemberRepository memberRepository;

    private final PasswordEncoder passwordEncoder;

    @Transactional
    public void memberSignup(MemberSignupRequest request) {
        if (memberRepository.existsByNickname(request.getNickname())) {
             throw new DuplicateNicknameException(ErrorType.DUPLICATE_NICKNAME);
        }
        if (memberRepository.existsByEmail(request.getEmail())) {
             throw new DuplicateEmailException(ErrorType.DUPLICATE_EMAIL);
        }
        String encoded = passwordEncoder.encode(request.getPassword());
        Member member = Member.builder()
                .name(request.getName())
                .nickname(request.getNickname())
                .email(request.getEmail())
                .password(encoded)
                .build();
        memberRepository.save(member);
    }

}
