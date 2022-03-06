package com.nameless.spin_off.entity.enums.post;

public enum PostContentLimitEnum {

    CONTENT_LENGTH_MAX(500),
    TITLE_LENGTH_MAX(100);


    private final int value;

    PostContentLimitEnum(int value) {
        this.value = value;
    }

    public int getValue() {
        return value;
    }
}
