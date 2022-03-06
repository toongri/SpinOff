package com.nameless.spin_off.exception.member;

public class AlreadyFollowedMemberException extends RuntimeException{
    public AlreadyFollowedMemberException() {
        super("이미 해당 유저를 팔로우 했습니다.");
    }

    public AlreadyFollowedMemberException(String message) {
        super(message);
    }

    public AlreadyFollowedMemberException(String message, Throwable cause) {
        super(message, cause);
    }

    public AlreadyFollowedMemberException(Throwable cause) {
        super(cause);
    }

    protected AlreadyFollowedMemberException(String message, Throwable cause, boolean enableSuppression, boolean writableStackTrace) {
        super(message, cause, enableSuppression, writableStackTrace);
    }
}
