package com.nameless.spin_off.exception.member;

public class EmailNotAuthenticatedException extends RuntimeException {
    public EmailNotAuthenticatedException() {
        super("이메일 인증이 필요합니다.");
    }

    public EmailNotAuthenticatedException(String message) {
        super(message);
    }

    public EmailNotAuthenticatedException(String message, Throwable cause) {
        super(message, cause);
    }

    public EmailNotAuthenticatedException(Throwable cause) {
        super(cause);
    }

    protected EmailNotAuthenticatedException(String message, Throwable cause, boolean enableSuppression, boolean writableStackTrace) {
        super(message, cause, enableSuppression, writableStackTrace);
    }
}
