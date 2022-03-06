package com.nameless.spin_off.exception.member;

public class AlreadyFollowedMovieException extends RuntimeException{
    public AlreadyFollowedMovieException() {
        super("이미 해당 영화를 팔로우 했습니다.");
    }

    public AlreadyFollowedMovieException(String message) {
        super(message);
    }

    public AlreadyFollowedMovieException(String message, Throwable cause) {
        super(message, cause);
    }

    public AlreadyFollowedMovieException(Throwable cause) {
        super(cause);
    }

    protected AlreadyFollowedMovieException(String message, Throwable cause, boolean enableSuppression, boolean writableStackTrace) {
        super(message, cause, enableSuppression, writableStackTrace);
    }
}
