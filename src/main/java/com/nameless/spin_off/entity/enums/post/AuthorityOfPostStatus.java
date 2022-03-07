package com.nameless.spin_off.entity.enums.post;

import com.nameless.spin_off.entity.enums.EnumMapperType;

public enum AuthorityOfPostStatus implements EnumMapperType {
    A("ADMIN"),
    B("DOCENT"),
    C("USER");

    private final String title;

    AuthorityOfPostStatus(String title) {
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
