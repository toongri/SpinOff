package com.nameless.spin_off.exception.member;

public class InCorrectNicknameException extends RuntimeException{
    public InCorrectNicknameException() {
        super("닉네임의 형식이 맞지 않습니다.");
    }

    public InCorrectNicknameException(String message) {
        super(message);
    }

    public InCorrectNicknameException(String message, Throwable cause) {
        super(message, cause);
    }

    public InCorrectNicknameException(Throwable cause) {
        super(cause);
    }

    protected InCorrectNicknameException(String message, Throwable cause, boolean enableSuppression, boolean writableStackTrace) {
        super(message, cause, enableSuppression, writableStackTrace);
    }
}
