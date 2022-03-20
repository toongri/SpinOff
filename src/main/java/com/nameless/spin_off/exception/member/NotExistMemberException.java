package com.nameless.spin_off.exception.member;

public class NotExistMemberException extends RuntimeException{
    public NotExistMemberException() {
        super("해당 유저는 존재하지 않습니다.");
    }

    public NotExistMemberException(String message) {
        super(message);
    }

    public NotExistMemberException(String message, Throwable cause) {
        super(message, cause);
    }

    public NotExistMemberException(Throwable cause) {
        super(cause);
    }

    protected NotExistMemberException(String message, Throwable cause, boolean enableSuppression, boolean writableStackTrace) {
        super(message, cause, enableSuppression, writableStackTrace);
    }
}
