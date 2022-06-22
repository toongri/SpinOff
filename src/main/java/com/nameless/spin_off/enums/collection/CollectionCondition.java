package com.nameless.spin_off.enums.collection;

import java.util.regex.Pattern;

import static com.nameless.spin_off.enums.ContentsLengthEnum.*;

public enum CollectionCondition {
    TITLE("^.{" + COLLECTION_TITLE_MIN.getLength() + "," + COLLECTION_TITLE_MAX.getLength() + "}$"),
    CONTENT("^.{" + COLLECTION_CONTENT_MIN.getLength() + "," + COLLECTION_CONTENT_MAX.getLength() + "}$");

    private final String possibleWord;

    CollectionCondition(String possibleWord) {
        this.possibleWord = possibleWord;
    }

    public boolean isNotCorrect(String word) {
        return !Pattern.matches(possibleWord, word);
    }
}
