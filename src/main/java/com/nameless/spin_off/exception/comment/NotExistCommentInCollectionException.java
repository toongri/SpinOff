package com.nameless.spin_off.exception.comment;

public class NotExistCommentInCollectionException extends RuntimeException{
    public NotExistCommentInCollectionException() {
        super("댓글이 존재하지 않습니다.");
    }

    public NotExistCommentInCollectionException(String message) {
        super(message);
    }

    public NotExistCommentInCollectionException(String message, Throwable cause) {
        super(message, cause);
    }

    public NotExistCommentInCollectionException(Throwable cause) {
        super(cause);
    }

    protected NotExistCommentInCollectionException(String message, Throwable cause, boolean enableSuppression, boolean writableStackTrace) {
        super(message, cause, enableSuppression, writableStackTrace);
    }
}
