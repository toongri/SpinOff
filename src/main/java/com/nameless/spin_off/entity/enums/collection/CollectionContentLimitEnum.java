package com.nameless.spin_off.entity.enums.collection;

public enum CollectionContentLimitEnum {
    CONTENT_LENGTH_MAX(500),
    TITLE_LENGTH_MAX(100);


    private final int value;

    CollectionContentLimitEnum(int value) {
        this.value = value;
    }

    public int getValue() {
        return value;
    }
}
