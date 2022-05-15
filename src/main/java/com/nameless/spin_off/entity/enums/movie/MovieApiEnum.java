package com.nameless.spin_off.entity.enums.movie;

public enum MovieApiEnum {
    API_REQUEST_NUMBER_MAX(29),
    API_REQUEST_LENGTH_MAX(100);

    private final int value;

    MovieApiEnum(int value) {
        this.value = value;
    }

    public int getValue() {
        return value;
    }
}
