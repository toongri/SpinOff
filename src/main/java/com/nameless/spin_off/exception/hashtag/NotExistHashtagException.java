package com.nameless.spin_off.exception.hashtag;

public class NotExistHashtagException extends RuntimeException{
    public NotExistHashtagException() {
        super("해당 해시태그는 존재하지 않습니다.");
    }

    public NotExistHashtagException(String message) {
        super(message);
    }

    public NotExistHashtagException(String message, Throwable cause) {
        super(message, cause);
    }

    public NotExistHashtagException(Throwable cause) {
        super(cause);
    }

    protected NotExistHashtagException(String message, Throwable cause, boolean enableSuppression, boolean writableStackTrace) {
        super(message, cause, enableSuppression, writableStackTrace);
    }
}
