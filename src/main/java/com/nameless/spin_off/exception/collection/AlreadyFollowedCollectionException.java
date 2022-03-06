package com.nameless.spin_off.exception.collection;

public class AlreadyFollowedCollectionException extends RuntimeException{
    public AlreadyFollowedCollectionException() {
        super("이미 해당 컬렉션을 팔로우 했습니다.");
    }

    public AlreadyFollowedCollectionException(String message) {
        super(message);
    }

    public AlreadyFollowedCollectionException(String message, Throwable cause) {
        super(message, cause);
    }

    public AlreadyFollowedCollectionException(Throwable cause) {
        super(cause);
    }

    protected AlreadyFollowedCollectionException(String message, Throwable cause, boolean enableSuppression, boolean writableStackTrace) {
        super(message, cause, enableSuppression, writableStackTrace);
    }
}
