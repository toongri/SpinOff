package com.nameless.spin_off.exception.movie;

public class NotExistMovieException extends RuntimeException{
    public NotExistMovieException() {
        super("해당 영화는 존재하지 않습니다.");
    }

    public NotExistMovieException(String message) {
        super(message);
    }

    public NotExistMovieException(String message, Throwable cause) {
        super(message, cause);
    }

    public NotExistMovieException(Throwable cause) {
        super(cause);
    }

    protected NotExistMovieException(String message, Throwable cause, boolean enableSuppression, boolean writableStackTrace) {
        super(message, cause, enableSuppression, writableStackTrace);
    }
}
