package com.nameless.spin_off.entity.enums.help;

import com.nameless.spin_off.entity.enums.EnumMapperType;

public enum ContentTypeStatus implements EnumMapperType {
    A("POST"),
    B("COLLECTION"),
    C("DM"),
    D("COMMENT_IN_COLLECTION"),
    E("COMMENT_IN_POST");

    private final String title;

    ContentTypeStatus(String title) {
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
