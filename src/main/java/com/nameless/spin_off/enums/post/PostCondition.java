package com.nameless.spin_off.enums.post;

import java.util.regex.Pattern;

import static com.nameless.spin_off.enums.ContentsLengthEnum.*;

public enum PostCondition {
    TITLE("^.{" + POST_TITLE_MIN.getLength() + "," + POST_TITLE_MAX.getLength() + "}$"),
    CONTENT("^.{" + POST_CONTENT_MIN.getLength() + "," + POST_CONTENT_MAX.getLength() + "}$");

    private final String possibleWord;

    PostCondition(String possibleWord) {
        this.possibleWord = possibleWord;
    }

    public boolean isNotCorrect(String word) {
        return !Pattern.matches(possibleWord, word);
    }
}
