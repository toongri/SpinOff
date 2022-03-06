package com.nameless.spin_off.exception.collection;

public class NotExistCollectionException extends RuntimeException{
    public NotExistCollectionException() {
        super("컬렉션이 존재하지 않습니다.");
    }

    public NotExistCollectionException(String message) {
        super(message);
    }

    public NotExistCollectionException(String message, Throwable cause) {
        super(message, cause);
    }

    public NotExistCollectionException(Throwable cause) {
        super(cause);
    }

    protected NotExistCollectionException(String message, Throwable cause, boolean enableSuppression, boolean writableStackTrace) {
        super(message, cause, enableSuppression, writableStackTrace);
    }
}
