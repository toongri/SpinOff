package com.nameless.spin_off.exception.collection;

import static com.nameless.spin_off.entity.enums.collection.CollectionContentLimitEnum.TITLE_LENGTH_MAX;

public class OverTitleOfCollectionException extends RuntimeException{
    public OverTitleOfCollectionException() {
        super("글의 제목은 "+ TITLE_LENGTH_MAX.getValue() +"자를 넘겨서는 안됩니다.");
    }

    public OverTitleOfCollectionException(String message) {
        super(message);
    }

    public OverTitleOfCollectionException(String message, Throwable cause) {
        super(message, cause);
    }

    public OverTitleOfCollectionException(Throwable cause) {
        super(cause);
    }

    protected OverTitleOfCollectionException(String message, Throwable cause, boolean enableSuppression, boolean writableStackTrace) {
        super(message, cause, enableSuppression, writableStackTrace);
    }
}
