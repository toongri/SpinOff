package com.nameless.spin_off.exception.member;

public class InCorrectAccountPwException extends RuntimeException{
    public InCorrectAccountPwException() {
        super("비밀번호의 형식이 맞지 않습니다.");
    }

    public InCorrectAccountPwException(String message) {
        super(message);
    }

    public InCorrectAccountPwException(String message, Throwable cause) {
        super(message, cause);
    }

    public InCorrectAccountPwException(Throwable cause) {
        super(cause);
    }

    protected InCorrectAccountPwException(String message, Throwable cause, boolean enableSuppression, boolean writableStackTrace) {
        super(message, cause, enableSuppression, writableStackTrace);
    }
}
