package com.nameless.spin_off.exception.post;

public class AlreadyLikedPostException extends RuntimeException{
    public AlreadyLikedPostException() {
        super("이미 해당 유저를 팔로우 했습니다.");
    }

    public AlreadyLikedPostException(String message) {
        super(message);
    }

    public AlreadyLikedPostException(String message, Throwable cause) {
        super(message, cause);
    }

    public AlreadyLikedPostException(Throwable cause) {
        super(cause);
    }

    protected AlreadyLikedPostException(String message, Throwable cause, boolean enableSuppression, boolean writableStackTrace) {
        super(message, cause, enableSuppression, writableStackTrace);
    }
}
