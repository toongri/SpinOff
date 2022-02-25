package com.nameless.spin_off.entity.enums;

import lombok.Getter;

@Getter
public class EnumMapperValue {

    private final String code;
    private final String title;

    public EnumMapperValue(EnumMapperType enumMapperType) {
        code = enumMapperType.getCode();
        title = enumMapperType.getTitle();
    }

    @Override
    public String toString() {
        return "{" +
                "code='" + code + '\'' +
                ", title=" + title +'\'' +
                '}';
    }

}
