package com.nameless.spin_off.exception.member;

public class AlreadyLinkageEmailException extends RuntimeException {
    public AlreadyLinkageEmailException() {
        super("이미 연동된 이메일입니다.");
    }

    public AlreadyLinkageEmailException(String message) {
        super(message);
    }

    public AlreadyLinkageEmailException(String message, Throwable cause) {
        super(message, cause);
    }

    public AlreadyLinkageEmailException(Throwable cause) {
        super(cause);
    }

    protected AlreadyLinkageEmailException(String message, Throwable cause, boolean enableSuppression, boolean writableStackTrace) {
        super(message, cause, enableSuppression, writableStackTrace);
    }
}
