package com.nameless.spin_off.exception.collection;

public class AlreadyCollectedPostException extends RuntimeException{
    public AlreadyCollectedPostException() {
        super("이미 컬렉션에 포스트가 포함되있습니다.");
    }

    public AlreadyCollectedPostException(String message) {
        super(message);
    }

    public AlreadyCollectedPostException(String message, Throwable cause) {
        super(message, cause);
    }

    public AlreadyCollectedPostException(Throwable cause) {
        super(cause);
    }

    protected AlreadyCollectedPostException(String message, Throwable cause, boolean enableSuppression, boolean writableStackTrace) {
        super(message, cause, enableSuppression, writableStackTrace);
    }
}
