package com.nameless.spin_off.entity.enums.member;

public enum EmailLinkageServiceEnum {
    NAVER("naver"),
    GOOGLE("gmail"),
    KAKAO("kakao");

    private final String value;

    EmailLinkageServiceEnum(String value) {
        this.value = value;
    }

    public String getValue() {
        return value;
    }
}
