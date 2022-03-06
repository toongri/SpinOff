package com.nameless.spin_off.exception.post;

import static com.nameless.spin_off.entity.enums.collection.CollectionContentLimitEnum.TITLE_LENGTH_MAX;

public class OverTitleOfPostException extends RuntimeException {
    public OverTitleOfPostException() {
        super("글의 제목은 "+ TITLE_LENGTH_MAX.getValue() +"자를 넘겨서는 안됩니다.");
    }

    public OverTitleOfPostException(String message) {
        super(message);
    }

    public OverTitleOfPostException(String message, Throwable cause) {
        super(message, cause);
    }

    public OverTitleOfPostException(Throwable cause) {
        super(cause);
    }

    protected OverTitleOfPostException(String message, Throwable cause, boolean enableSuppression, boolean writableStackTrace) {
        super(message, cause, enableSuppression, writableStackTrace);
    }
}
