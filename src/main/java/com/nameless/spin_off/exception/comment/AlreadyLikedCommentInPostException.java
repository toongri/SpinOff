package com.nameless.spin_off.exception.comment;

public class AlreadyLikedCommentInPostException extends RuntimeException{
    public AlreadyLikedCommentInPostException() {
        super("이미 해당 댓글을 좋아요 했습니다.");
    }

    public AlreadyLikedCommentInPostException(String message) {
        super(message);
    }

    public AlreadyLikedCommentInPostException(String message, Throwable cause) {
        super(message, cause);
    }

    public AlreadyLikedCommentInPostException(Throwable cause) {
        super(cause);
    }

    protected AlreadyLikedCommentInPostException(String message, Throwable cause, boolean enableSuppression, boolean writableStackTrace) {
        super(message, cause, enableSuppression, writableStackTrace);
    }
}
