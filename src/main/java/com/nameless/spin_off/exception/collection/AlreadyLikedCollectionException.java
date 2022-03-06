package com.nameless.spin_off.exception.collection;

public class AlreadyLikedCollectionException extends RuntimeException{
    public AlreadyLikedCollectionException() {
        super("이미 해당 컬렉션을 좋아요 했습니다.");
    }

    public AlreadyLikedCollectionException(String message) {
        super(message);
    }

    public AlreadyLikedCollectionException(String message, Throwable cause) {
        super(message, cause);
    }

    public AlreadyLikedCollectionException(Throwable cause) {
        super(cause);
    }

    protected AlreadyLikedCollectionException(String message, Throwable cause, boolean enableSuppression, boolean writableStackTrace) {
        super(message, cause, enableSuppression, writableStackTrace);
    }
}
