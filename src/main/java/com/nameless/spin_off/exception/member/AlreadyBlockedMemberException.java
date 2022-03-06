package com.nameless.spin_off.exception.member;

public class AlreadyBlockedMemberException extends RuntimeException{
    public AlreadyBlockedMemberException() {
        super("이미 해당 유저를 차단 했습니다.");
    }

    public AlreadyBlockedMemberException(String message) {
        super(message);
    }

    public AlreadyBlockedMemberException(String message, Throwable cause) {
        super(message, cause);
    }

    public AlreadyBlockedMemberException(Throwable cause) {
        super(cause);
    }

    protected AlreadyBlockedMemberException(String message, Throwable cause, boolean enableSuppression, boolean writableStackTrace) {
        super(message, cause, enableSuppression, writableStackTrace);
    }
}
