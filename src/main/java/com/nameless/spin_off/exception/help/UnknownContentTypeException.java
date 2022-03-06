package com.nameless.spin_off.exception.help;

public class UnknownContentTypeException extends RuntimeException{
    public UnknownContentTypeException() {
        super("해당 컨텐츠 속성은 존재하지 않습니다.");
    }

    public UnknownContentTypeException(String message) {
        super(message);
    }

    public UnknownContentTypeException(String message, Throwable cause) {
        super(message, cause);
    }

    public UnknownContentTypeException(Throwable cause) {
        super(cause);
    }

    protected UnknownContentTypeException(String message, Throwable cause, boolean enableSuppression, boolean writableStackTrace) {
        super(message, cause, enableSuppression, writableStackTrace);
    }
}
