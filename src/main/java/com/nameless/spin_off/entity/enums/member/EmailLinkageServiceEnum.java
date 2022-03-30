package com.nameless.spin_off.entity.enums.member;

public enum EmailLinkageServiceEnum {
    naver("naver"),
    google("gmail"),
    kakao("kakao");

    private final String value;

    EmailLinkageServiceEnum(String value) {
        this.value = value;
    }

    public String getValue() {
        return value;
    }

    public String getName() {
        return name();
    }
}
