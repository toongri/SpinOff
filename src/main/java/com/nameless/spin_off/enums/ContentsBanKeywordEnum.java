package com.nameless.spin_off.enums;

import java.util.List;

public enum ContentsBanKeywordEnum {

    MOVIE(List.of("성인", "에로"));

    private final List<String> keywords;

    ContentsBanKeywordEnum(List<String> keywords) {
        this.keywords = keywords;
    }

    public boolean isIn(String keyword) {
        return keywords.stream().anyMatch(keyword::contains);
    }


}
