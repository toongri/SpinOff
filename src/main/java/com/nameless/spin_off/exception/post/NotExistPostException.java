package com.nameless.spin_off.exception.post;

public class NotExistPostException extends RuntimeException {
    public NotExistPostException() {
        super("해당 포스트가 존재하지 않습니다.");
    }

    public NotExistPostException(String message) {
        super(message);
    }

    public NotExistPostException(String message, Throwable cause) {
        super(message, cause);
    }

    public NotExistPostException(Throwable cause) {
        super(cause);
    }

    protected NotExistPostException(String message, Throwable cause, boolean enableSuppression, boolean writableStackTrace) {
        super(message, cause, enableSuppression, writableStackTrace);
    }
}
