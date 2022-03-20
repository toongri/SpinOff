package com.nameless.spin_off.exception.member;

public class InCorrectEmailException extends RuntimeException{
    public InCorrectEmailException() {
        super("이메일의 형식이 맞지 않습니다.");
    }

    public InCorrectEmailException(String message) {
        super(message);
    }

    public InCorrectEmailException(String message, Throwable cause) {
        super(message, cause);
    }

    public InCorrectEmailException(Throwable cause) {
        super(cause);
    }

    protected InCorrectEmailException(String message, Throwable cause, boolean enableSuppression, boolean writableStackTrace) {
        super(message, cause, enableSuppression, writableStackTrace);
    }
}
