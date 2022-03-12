package com.nameless.spin_off.exception.member;

public class NotExistEmailLinkageTokenException extends RuntimeException{
    public NotExistEmailLinkageTokenException() {
        super("현재 이메일 연동 토큰을 찾을 수 없습니다.");
    }

    public NotExistEmailLinkageTokenException(String message) {
        super(message);
    }

    public NotExistEmailLinkageTokenException(String message, Throwable cause) {
        super(message, cause);
    }

    public NotExistEmailLinkageTokenException(Throwable cause) {
        super(cause);
    }

    protected NotExistEmailLinkageTokenException(String message, Throwable cause, boolean enableSuppression, boolean writableStackTrace) {
        super(message, cause, enableSuppression, writableStackTrace);
    }
}
