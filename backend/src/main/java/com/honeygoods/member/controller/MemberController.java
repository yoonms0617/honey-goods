package com.honeygoods.member.controller;

import com.honeygoods.member.dto.MemberSignupRequest;
import com.honeygoods.member.service.MemberService;

import lombok.RequiredArgsConstructor;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;

@RestController
@RequestMapping("/api/member")
@RequiredArgsConstructor
public class MemberController {

    private final MemberService memberService;

    @PostMapping("/signup")
    public ResponseEntity<Void> memberSignup(@Valid @RequestBody MemberSignupRequest request) {
        memberService.memberSignup(request);
        return ResponseEntity.ok().build();
    }

}
