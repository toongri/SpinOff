package com.nameless.spin_off.entity.enums.post;

import com.nameless.spin_off.entity.enums.EnumMapperType;

public enum PublicOfPostStatus implements EnumMapperType {
    A("PUBLIC"),
    B("PRIVATE"),
    C("FOLLOWER");

    private final String title;

    PublicOfPostStatus(String title) {
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
