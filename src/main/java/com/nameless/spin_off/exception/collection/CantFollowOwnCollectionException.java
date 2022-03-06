package com.nameless.spin_off.exception.collection;

public class CantFollowOwnCollectionException extends RuntimeException{
    public CantFollowOwnCollectionException() {
        super("본인의 컬렉션은 팔로우 할 수 없습니다.");
    }

    public CantFollowOwnCollectionException(String message) {
        super(message);
    }

    public CantFollowOwnCollectionException(String message, Throwable cause) {
        super(message, cause);
    }

    public CantFollowOwnCollectionException(Throwable cause) {
        super(cause);
    }

    protected CantFollowOwnCollectionException(String message, Throwable cause, boolean enableSuppression, boolean writableStackTrace) {
        super(message, cause, enableSuppression, writableStackTrace);
    }
}
