package com.nameless.spin_off.exception.member;

public class InCorrectAccountIdException extends RuntimeException{
    public InCorrectAccountIdException() {
        super("아이디의 형식이 맞지 않습니다.");
    }

    public InCorrectAccountIdException(String message) {
        super(message);
    }

    public InCorrectAccountIdException(String message, Throwable cause) {
        super(message, cause);
    }

    public InCorrectAccountIdException(Throwable cause) {
        super(cause);
    }

    protected InCorrectAccountIdException(String message, Throwable cause, boolean enableSuppression, boolean writableStackTrace) {
        super(message, cause, enableSuppression, writableStackTrace);
    }
}
