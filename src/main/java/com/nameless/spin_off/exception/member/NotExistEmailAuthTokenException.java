package com.nameless.spin_off.exception.member;

public class NotExistEmailAuthTokenException extends RuntimeException{
    public NotExistEmailAuthTokenException() {
        super("해당 이메일 인증이 존재하지 않습니다.");
    }

    public NotExistEmailAuthTokenException(String message) {
        super(message);
    }

    public NotExistEmailAuthTokenException(String message, Throwable cause) {
        super(message, cause);
    }

    public NotExistEmailAuthTokenException(Throwable cause) {
        super(cause);
    }

    protected NotExistEmailAuthTokenException(String message, Throwable cause, boolean enableSuppression, boolean writableStackTrace) {
        super(message, cause, enableSuppression, writableStackTrace);
    }
}
