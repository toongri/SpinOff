package com.nameless.spin_off.enums;

public enum ContentsLengthEnum {
    HASHTAG_LIST_MAX(30),
    HASHTAG_CONTENTS_MIN(1),
    HASHTAG_CONTENTS_MAX(30),
    ACCOUNT_ID_MIN(6),
    ACCOUNT_ID_MAX(12),
    ACCOUNT_PW_MIN(8),
    ACCOUNT_PW_MAX(16),
    NICKNAME_MIN(2),
    NICKNAME_MAX(8),
    BIO_MIN(0),
    BIO_MAX(150),
    EMAIL_TOKEN(8),
    POST_IMAGE_MAX(5),
    RELATED_POST_MIN_TAG(5),
    POST_TITLE_MIN(1),
    POST_TITLE_MAX(100),
    POST_CONTENT_MIN(0),
    POST_CONTENT_MAX(500),
    COLLECTION_TITLE_MIN(1),
    COLLECTION_TITLE_MAX(100),
    COLLECTION_CONTENT_MIN(0),
    COLLECTION_CONTENT_MAX(500);

    private final int length;

    ContentsLengthEnum(int length) {
        this.length = length;
    }

    public int getLength() {
        return length;
    }
}
