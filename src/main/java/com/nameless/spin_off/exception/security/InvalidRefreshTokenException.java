package com.nameless.spin_off.exception.security;

public class InvalidRefreshTokenException extends RuntimeException{
    public InvalidRefreshTokenException() {
        super("토큰이 유효하지 않습니다.");
    }

    public InvalidRefreshTokenException(String message) {
        super(message);
    }

    public InvalidRefreshTokenException(String message, Throwable cause) {
        super(message, cause);
    }

    public InvalidRefreshTokenException(Throwable cause) {
        super(cause);
    }

    protected InvalidRefreshTokenException(String message, Throwable cause, boolean enableSuppression, boolean writableStackTrace) {
        super(message, cause, enableSuppression, writableStackTrace);
    }
}
