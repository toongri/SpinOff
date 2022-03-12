package com.nameless.spin_off.exception.member;

public class NotExistEmailAuthTokenException extends RuntimeException{
    public NotExistEmailAuthTokenException() {
        super();
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
