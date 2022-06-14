package com.nameless.spin_off.enums.movie;

public enum MovieApiEnum {
    KOBIS_API_REQUEST_NUMBER_MAX(25),
    KOBIS_API_REQUEST_SIZE_MAX(10),
    NAVER_API_REQUEST_NUMBER_MAX(20000);


    private final int value;

    MovieApiEnum(int value) {
        this.value = value;
    }

    public int getValue() {
        return value;
    }
}
