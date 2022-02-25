package com.nameless.spin_off.entity.enums.member;

import com.nameless.spin_off.entity.enums.EnumMapperType;

public enum SearchedByMemberStatus implements EnumMapperType {
    A("POST"),
    B("COLLECTION"),
    C("MOVIE"),
    D("MEMBER");

    private final String title;

    SearchedByMemberStatus(String title) {
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
