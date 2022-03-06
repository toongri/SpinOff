package com.nameless.spin_off.exception.collection;

public class NotMatchCollectionException extends RuntimeException {
    public NotMatchCollectionException() {
        super("컬렉션 id가 옳바르지 않습니다.");
    }

    public NotMatchCollectionException(String message) {
        super(message);
    }

    public NotMatchCollectionException(String message, Throwable cause) {
        super(message, cause);
    }

    public NotMatchCollectionException(Throwable cause) {
        super(cause);
    }

    protected NotMatchCollectionException(String message, Throwable cause, boolean enableSuppression, boolean writableStackTrace) {
        super(message, cause, enableSuppression, writableStackTrace);
    }
}
