package com.nameless.spin_off.exception.collection;

import static com.nameless.spin_off.entity.enums.collection.CollectionContentLimitEnum.CONTENT_LENGTH_MAX;

public class OverContentOfCollectionException extends RuntimeException{
    public OverContentOfCollectionException() {
        super("글의 본문은 "+ CONTENT_LENGTH_MAX.getValue() +"자를 넘겨서는 안됩니다.");
    }

    public OverContentOfCollectionException(String message) {
        super(message);
    }

    public OverContentOfCollectionException(String message, Throwable cause) {
        super(message, cause);
    }

    public OverContentOfCollectionException(Throwable cause) {
        super(cause);
    }

    protected OverContentOfCollectionException(String message, Throwable cause, boolean enableSuppression, boolean writableStackTrace) {
        super(message, cause, enableSuppression, writableStackTrace);
    }
}
