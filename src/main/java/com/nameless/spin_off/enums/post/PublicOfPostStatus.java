package com.nameless.spin_off.enums.post;

import com.nameless.spin_off.enums.EnumMapperType;

public enum PublicOfPostStatus implements EnumMapperType {
    A("전체공개"),
    B("비공개"),
    C("팔로잉");

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
