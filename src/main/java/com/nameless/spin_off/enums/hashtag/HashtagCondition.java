package com.nameless.spin_off.enums.hashtag;

import java.util.regex.Pattern;

import static com.nameless.spin_off.enums.ContentsLengthEnum.HASHTAG_CONTENTS_MAX;
import static com.nameless.spin_off.enums.ContentsLengthEnum.HASHTAG_CONTENTS_MIN;

public enum HashtagCondition {
    CONTENT("^[a-zA-Z0-9가-힣]{" + HASHTAG_CONTENTS_MIN.getLength() + "}[a-zA-Z0-9가-힣_]{0," +
            (HASHTAG_CONTENTS_MAX.getLength() - HASHTAG_CONTENTS_MIN.getLength()) + "}$");

    private final String possibleWord;


    HashtagCondition(String possibleWord) {
        this.possibleWord = possibleWord;
    }

    public boolean isNotCorrect(String word) {
        return !Pattern.matches(possibleWord, word);
    }
}
