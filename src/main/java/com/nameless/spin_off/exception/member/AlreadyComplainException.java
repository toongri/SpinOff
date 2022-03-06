package com.nameless.spin_off.exception.member;

public class AlreadyComplainException extends RuntimeException{
    public AlreadyComplainException() {
        super("이미 신고가 접수 되었습니다.");
    }

    public AlreadyComplainException(String message) {
        super(message);
    }

    public AlreadyComplainException(String message, Throwable cause) {
        super(message, cause);
    }

    public AlreadyComplainException(Throwable cause) {
        super(cause);
    }

    protected AlreadyComplainException(String message, Throwable cause, boolean enableSuppression, boolean writableStackTrace) {
        super(message, cause, enableSuppression, writableStackTrace);
    }
}
