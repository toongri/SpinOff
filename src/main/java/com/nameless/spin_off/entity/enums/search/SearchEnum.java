package com.nameless.spin_off.entity.enums.search;

public enum SearchEnum {
    MOVIE_SEARCH_THUMBNAIL_NUMBER(10),
    RELATED_SEARCH_KEYWORD_MAX_STR(10),
    RELATED_SEARCH_KEYWORD_MIN_STR(2);

    private final int value;

    SearchEnum(int value) {
        this.value = value;
    }

    public int getValue() {
        return value;
    }
}
