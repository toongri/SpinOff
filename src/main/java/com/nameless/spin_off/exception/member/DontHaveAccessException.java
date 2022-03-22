package com.nameless.spin_off.exception.member;

public class DontHaveAccessException extends RuntimeException {
    public DontHaveAccessException() {
        super("접근권한이 없습니다.");
    }

    public DontHaveAccessException(String message) {
        super(message);
    }

    public DontHaveAccessException(String message, Throwable cause) {
        super(message, cause);
    }

    public DontHaveAccessException(Throwable cause) {
        super(cause);
    }

    protected DontHaveAccessException(String message, Throwable cause, boolean enableSuppression, boolean writableStackTrace) {
        super(message, cause, enableSuppression, writableStackTrace);
    }
}
