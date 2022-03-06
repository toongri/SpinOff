package com.nameless.spin_off.exception.member;

public class AlreadyNicknameException extends RuntimeException{
    public AlreadyNicknameException() {
        super("이미 존재하는 닉네임입니다.");
    }

    public AlreadyNicknameException(String message) {
        super(message);
    }

    public AlreadyNicknameException(String message, Throwable cause) {
        super(message, cause);
    }

    public AlreadyNicknameException(Throwable cause) {
        super(cause);
    }

    protected AlreadyNicknameException(String message, Throwable cause, boolean enableSuppression, boolean writableStackTrace) {
        super(message, cause, enableSuppression, writableStackTrace);
    }
}
