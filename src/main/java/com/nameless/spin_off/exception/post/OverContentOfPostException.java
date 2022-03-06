package com.nameless.spin_off.exception.post;

import static com.nameless.spin_off.entity.enums.collection.CollectionContentLimitEnum.CONTENT_LENGTH_MAX;

public class OverContentOfPostException extends RuntimeException {
    public OverContentOfPostException() {
        super("글의 본문은 "+ CONTENT_LENGTH_MAX.getValue() +"자를 넘겨서는 안됩니다.");
    }

    public OverContentOfPostException(String message) {
        super(message);
    }

    public OverContentOfPostException(String message, Throwable cause) {
        super(message, cause);
    }

    public OverContentOfPostException(Throwable cause) {
        super(cause);
    }

    protected OverContentOfPostException(String message, Throwable cause, boolean enableSuppression, boolean writableStackTrace) {
        super(message, cause, enableSuppression, writableStackTrace);
    }
}
