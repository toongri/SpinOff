package com.nameless.spin_off.exception.search;

import static com.nameless.spin_off.entity.enums.search.SearchEnum.RELATED_SEARCH_KEYWORD_MIN_STR;

public class UnderLengthRelatedKeywordException extends RuntimeException{
    public UnderLengthRelatedKeywordException() {
        super("키워드는 "+ RELATED_SEARCH_KEYWORD_MIN_STR.getValue() +"자를 넘어야만 합니다.");
    }

    public UnderLengthRelatedKeywordException(String message) {
        super(message);
    }

    public UnderLengthRelatedKeywordException(String message, Throwable cause) {
        super(message, cause);
    }

    public UnderLengthRelatedKeywordException(Throwable cause) {
        super(cause);
    }

    protected UnderLengthRelatedKeywordException(String message, Throwable cause, boolean enableSuppression, boolean writableStackTrace) {
        super(message, cause, enableSuppression, writableStackTrace);
    }
}
