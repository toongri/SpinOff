package com.nameless.spin_off.exception.comment;

public class AlreadyLikedCommentInCollectionException extends RuntimeException{
    public AlreadyLikedCommentInCollectionException() {
        super("이미 해당 댓글을 좋아요 했습니다.");
    }

    public AlreadyLikedCommentInCollectionException(String message) {
        super(message);
    }

    public AlreadyLikedCommentInCollectionException(String message, Throwable cause) {
        super(message, cause);
    }

    public AlreadyLikedCommentInCollectionException(Throwable cause) {
        super(cause);
    }

    protected AlreadyLikedCommentInCollectionException(String message, Throwable cause, boolean enableSuppression, boolean writableStackTrace) {
        super(message, cause, enableSuppression, writableStackTrace);
    }
}
