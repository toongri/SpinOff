package com.nameless.spin_off.enums.member;

public enum EmailAuthProviderStatus {
    A("register"),
    B("authEmail"),
    C("updateEmail");

    private final String value;

    EmailAuthProviderStatus(String value) {
        this.value = value;
    }

    public String getValue() {
        return value;
    }
}
