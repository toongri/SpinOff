package com.nameless.spin_off.entity.enums.member;

import java.util.regex.Pattern;

import static com.nameless.spin_off.entity.enums.ContentsLengthEnum.*;

public enum MemberCondition {
    ACCOUNT_ID("^[a-zA-Z0-9_./]{" + ACCOUNT_ID_MIN.getLength() + "," + ACCOUNT_ID_MAX.getLength() + "}$"),
    ACCOUNT_PW("^[a-zA-Z0-9:!\"#$%&'()*+,-./:=?@[＼;]^_`{|}~]{" +
            ACCOUNT_PW_MIN.getLength() + "," + ACCOUNT_PW_MAX.getLength() + "}$"),
    NICKNAME("^[a-z0-9A-Z가-힣_,.]{" + NICKNAME_MIN.getLength() + "," + NICKNAME_MAX.getLength() + "}$"),
    CELL_PHONE("^01(?:0|1|[6-9])-\\d{3,4}-\\d{4}"),
    EMAIL("^[\\w!#$%&'*+/=?`{|}~^-]+(?:\\.[\\w!#$%&'*+/=?`{|}~^-]+)*@(?:[a-zA-Z0-9-]+\\.)+[a-zA-Z]{2,6}$"),
    ENGLISH("^[a-zA-Z]*$"),
    NUMBER("^[0-9]*$"),
    SIGN("^[:!\"#$%&'()*+,-./:=?@[＼;]^_`{|}~]*$");

    private final String possibleWord;

    MemberCondition(String possibleWord) {
        this.possibleWord = possibleWord;
    }

    public boolean isNotCorrect(String word) {
        return !Pattern.matches(possibleWord, word);
    }
}
