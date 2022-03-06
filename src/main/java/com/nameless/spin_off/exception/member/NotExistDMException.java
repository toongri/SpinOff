package com.nameless.spin_off.exception.member;

public class NotExistDMException extends RuntimeException{
    public NotExistDMException() {
        super("해당 DM은 존재하지 않습니다.");
    }

    public NotExistDMException(String message) {
        super(message);
    }

    public NotExistDMException(String message, Throwable cause) {
        super(message, cause);
    }

    public NotExistDMException(Throwable cause) {
        super(cause);
    }

    protected NotExistDMException(String message, Throwable cause, boolean enableSuppression, boolean writableStackTrace) {
        super(message, cause, enableSuppression, writableStackTrace);
    }
}
