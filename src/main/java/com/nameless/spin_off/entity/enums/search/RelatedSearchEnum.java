package com.nameless.spin_off.entity.enums.search;

public enum RelatedSearchEnum {
    RELATED_SEARCH_HASHTAG_NUMBER(10),
    RELATED_SEARCH_MEMBER_NUMBER(10),
    RELATED_SEARCH_ALL_NUMBER(2),
    RELATED_SEARCH_KEYWORD_MAX_STR(10),
    RELATED_SEARCH_KEYWORD_MIN_STR(2),
    LAST_SEARCH_NUMBER(5),
    MOST_POPULAR_HASHTAG_NUMBER(5);

    private final int value;

    RelatedSearchEnum(int value) {
        this.value = value;
    }

    public int getValue() {
        return value;
    }
}
