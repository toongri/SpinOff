package com.nameless.spin_off.entity.enums.member;

import com.nameless.spin_off.entity.enums.EnumMapperType;

public enum AuthorityOfMemberStatus implements EnumMapperType {
    A("ADMIN"),
    B("SELLER"),
    C("DOCENT");

    private final String title;

    AuthorityOfMemberStatus(String title) {
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
