package com.nameless.spin_off.entity.enums.collection;

import com.nameless.spin_off.entity.enums.EnumMapperType;

public enum PublicOfCollectionStatus implements EnumMapperType {
    A("PUBLIC"),
    B("PRIVATE"),
    C("FOLLOWER");

    private final String title;

    PublicOfCollectionStatus(String title) {
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
