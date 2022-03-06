package com.nameless.spin_off.exception.search;

import static com.nameless.spin_off.entity.enums.search.SearchEnum.RELATED_SEARCH_KEYWORD_MAX_STR;

public class OverLengthRelatedKeywordException extends RuntimeException{
    public OverLengthRelatedKeywordException() {
        super("키워드는 "+ RELATED_SEARCH_KEYWORD_MAX_STR.getValue() +"자를 넘겨서는 안됩니다.");
    }

    public OverLengthRelatedKeywordException(String message) {
        super(message);
    }

    public OverLengthRelatedKeywordException(String message, Throwable cause) {
        super(message, cause);
    }

    public OverLengthRelatedKeywordException(Throwable cause) {
        super(cause);
    }

    protected OverLengthRelatedKeywordException(String message, Throwable cause, boolean enableSuppression, boolean writableStackTrace) {
        super(message, cause, enableSuppression, writableStackTrace);
    }
}
