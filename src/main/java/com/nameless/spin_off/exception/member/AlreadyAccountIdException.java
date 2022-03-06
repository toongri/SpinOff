package com.nameless.spin_off.exception.member;

public class AlreadyAccountIdException extends RuntimeException{
    public AlreadyAccountIdException() {
        super("해당 아이디는 이미 존재합니다.");
    }

    public AlreadyAccountIdException(String message) {
        super(message);
    }

    public AlreadyAccountIdException(String message, Throwable cause) {
        super(message, cause);
    }

    public AlreadyAccountIdException(Throwable cause) {
        super(cause);
    }

    protected AlreadyAccountIdException(String message, Throwable cause, boolean enableSuppression, boolean writableStackTrace) {
        super(message, cause, enableSuppression, writableStackTrace);
    }
}
