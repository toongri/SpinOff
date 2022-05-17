package com.nameless.spin_off.enums.member;

import com.nameless.spin_off.enums.EnumMapperType;

public enum BlockedMemberStatus implements EnumMapperType {
    A("ALL"),
    B("DM");

    private final String title;

    BlockedMemberStatus(String title) {
        this.title = title;
    }

    @Override
    public String getCode() {
        return name();
    }

    @Override
    public String getTitle() {
        return title;
    }
}
