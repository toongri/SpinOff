package com.nameless.spin_off.exception.post;

public class AlreadyPostedHashtagException extends RuntimeException{
    public AlreadyPostedHashtagException() {
        super("이미 해당 해시태그를 태그 했습니다.");
    }

    public AlreadyPostedHashtagException(String message) {
        super(message);
    }

    public AlreadyPostedHashtagException(String message, Throwable cause) {
        super(message, cause);
    }

    public AlreadyPostedHashtagException(Throwable cause) {
        super(cause);
    }

    protected AlreadyPostedHashtagException(String message, Throwable cause, boolean enableSuppression, boolean writableStackTrace) {
        super(message, cause, enableSuppression, writableStackTrace);
    }
}
