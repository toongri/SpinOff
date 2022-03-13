package com.nameless.spin_off.exception.member;

public class NotCorrectEmailRequest extends RuntimeException{
    public NotCorrectEmailRequest() {
        super("요청한 이메일이 일치하지 않습니다.");
    }

    public NotCorrectEmailRequest(String message) {
        super(message);
    }

    public NotCorrectEmailRequest(String message, Throwable cause) {
        super(message, cause);
    }

    public NotCorrectEmailRequest(Throwable cause) {
        super(cause);
    }

    protected NotCorrectEmailRequest(String message, Throwable cause, boolean enableSuppression, boolean writableStackTrace) {
        super(message, cause, enableSuppression, writableStackTrace);
    }
}
