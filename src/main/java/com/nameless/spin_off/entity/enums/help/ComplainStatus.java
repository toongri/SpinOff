package com.nameless.spin_off.entity.enums.help;

import com.nameless.spin_off.entity.enums.EnumMapperType;

public enum ComplainStatus implements EnumMapperType {
    A("TYPE1"),
    B("TYPE2"),
    C("TYPE3");

    private final String title;

    ComplainStatus(String title) {
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