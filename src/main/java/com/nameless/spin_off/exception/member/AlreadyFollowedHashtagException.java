package com.nameless.spin_off.exception.member;

public class AlreadyFollowedHashtagException extends RuntimeException {
    public AlreadyFollowedHashtagException() {
        super("이미 해당 해시태그를 팔로우 했습니다.");
    }

    public AlreadyFollowedHashtagException(String message) {
        super(message);
    }

    public AlreadyFollowedHashtagException(String message, Throwable cause) {
        super(message, cause);
    }

    public AlreadyFollowedHashtagException(Throwable cause) {
        super(cause);
    }

    protected AlreadyFollowedHashtagException(String message, Throwable cause, boolean enableSuppression, boolean writableStackTrace) {
        super(message, cause, enableSuppression, writableStackTrace);
    }
}
