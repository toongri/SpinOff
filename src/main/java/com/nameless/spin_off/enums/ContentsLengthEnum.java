package com.nameless.spin_off.enums;

public enum ContentsLengthEnum {
    HASHTAG_LIST_MAX(30),
    HASHTAG_CONTENTS_MIN(1),
    HASHTAG_CONTENTS_MAX(30),
    ACCOUNT_ID_MIN(6),
    ACCOUNT_ID_MAX(12),
    ACCOUNT_PW_MIN(8),
    ACCOUNT_PW_MAX(100),
    NICKNAME_MIN(2),
    NICKNAME_MAX(8),
    EMAIL_TOKEN(8),
    POST_IMAGE_MAX(5),
    RELATED_POST_MIN_TAG(5);

    private final int length;

    ContentsLengthEnum(int length) {
        this.length = length;
    }

    public int getLength() {
        return length;
    }
}
