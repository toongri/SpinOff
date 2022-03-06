package com.nameless.spin_off.exception.hashtag;

public class InCorrectHashtagContentException extends RuntimeException{
    public InCorrectHashtagContentException() {
        super("해시태그에 금칙어가 들어있습니다.");
    }

    public InCorrectHashtagContentException(String message) {
        super(message);
    }

    public InCorrectHashtagContentException(String message, Throwable cause) {
        super(message, cause);
    }

    public InCorrectHashtagContentException(Throwable cause) {
        super(cause);
    }

    protected InCorrectHashtagContentException(String message, Throwable cause, boolean enableSuppression, boolean writableStackTrace) {
        super(message, cause, enableSuppression, writableStackTrace);
    }
}
