package com.nameless.spin_off.enums.help;

import com.nameless.spin_off.enums.EnumMapperType;

public enum InquirePublicStatus implements EnumMapperType {
    A("PUBLIC"),
    B("PRIVATE");

    private final String title;

    InquirePublicStatus(String title) {
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
