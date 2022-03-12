package com.nameless.spin_off.entity.enums.member;

public enum EmailLinkageProviderStatus {
    A("google"),
    B("naver"),
    C("kakao");

    private final String value;

    EmailLinkageProviderStatus(String value) {
        this.value = value;
    }

    public String getValue() {
        return value;
    }
}
