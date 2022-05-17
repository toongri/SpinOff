package com.nameless.spin_off.enums.member;

public enum EmailAuthProviderStatus {
    A("email"),
    B("idFind"),
    C("pwFind");

    private final String value;

    EmailAuthProviderStatus(String value) {
        this.value = value;
    }

    public String getValue() {
        return value;
    }
}
