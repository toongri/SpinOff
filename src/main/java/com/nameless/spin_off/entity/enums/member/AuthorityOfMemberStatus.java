package com.nameless.spin_off.entity.enums.member;

import com.nameless.spin_off.entity.enums.EnumMapperType;

public enum AuthorityOfMemberStatus implements EnumMapperType {
    A("ROLE_ADMIN", "관리자"),
    B("ROLE_DOCENT", "도슨트"),
    C("ROLE_USER", "일반 사용자");

    private final String key;
    private final String title;

    AuthorityOfMemberStatus(String key, String title) {
        this.key = key;
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
    public String getKey(){return key;}
}
