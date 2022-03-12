package com.nameless.spin_off.exception.member;

public class AlreadyEmailException extends RuntimeException{
    public AlreadyEmailException() {
        super("해당 이메일은 이미 존재합니다.");
    }

    public AlreadyEmailException(String message) {
        super(message);
    }

    public AlreadyEmailException(String message, Throwable cause) {
        super(message, cause);
    }

    public AlreadyEmailException(Throwable cause) {
        super(cause);
    }

    protected AlreadyEmailException(String message, Throwable cause, boolean enableSuppression, boolean writableStackTrace) {
        super(message, cause, enableSuppression, writableStackTrace);
    }
}
