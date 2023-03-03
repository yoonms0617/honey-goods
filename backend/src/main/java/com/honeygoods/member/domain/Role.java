package com.honeygoods.member.domain;

import lombok.Getter;

@Getter
public enum Role {

    MEMBER("ROLE_MEMBER", "회원");

    private final String key;

    private final String value;

    Role(String key, String value) {
        this.key = key;
        this.value = value;
    }

}
