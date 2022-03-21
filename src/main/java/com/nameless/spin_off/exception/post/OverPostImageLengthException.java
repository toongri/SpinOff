package com.nameless.spin_off.exception.post;

public class OverPostImageLengthException extends RuntimeException {
    public OverPostImageLengthException() {
        super("글에 허용되는 이미지의 갯수는 5개가 최대입니다.");
    }

    public OverPostImageLengthException(String message) {
        super(message);
    }

    public OverPostImageLengthException(String message, Throwable cause) {
        super(message, cause);
    }

    public OverPostImageLengthException(Throwable cause) {
        super(cause);
    }

    protected OverPostImageLengthException(String message, Throwable cause, boolean enableSuppression, boolean writableStackTrace) {
        super(message, cause, enableSuppression, writableStackTrace);
    }
}
