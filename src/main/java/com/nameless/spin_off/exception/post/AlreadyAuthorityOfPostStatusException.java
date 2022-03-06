package com.nameless.spin_off.exception.post;

public class AlreadyAuthorityOfPostStatusException extends RuntimeException{
    public AlreadyAuthorityOfPostStatusException() {
        super();
    }

    public AlreadyAuthorityOfPostStatusException(String message) {
        super(message);
    }

    public AlreadyAuthorityOfPostStatusException(String message, Throwable cause) {
        super(message, cause);
    }

    public AlreadyAuthorityOfPostStatusException(Throwable cause) {
        super(cause);
    }

    protected AlreadyAuthorityOfPostStatusException(String message, Throwable cause, boolean enableSuppression, boolean writableStackTrace) {
        super(message, cause, enableSuppression, writableStackTrace);
    }
}
