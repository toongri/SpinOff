package com.nameless.spin_off.exception.comment;

public class NotExistCommentInPostException extends RuntimeException{
    public NotExistCommentInPostException() {
        super("댓글이 존재하지 않습니다.");
    }

    public NotExistCommentInPostException(String message) {
        super(message);
    }

    public NotExistCommentInPostException(String message, Throwable cause) {
        super(message, cause);
    }

    public NotExistCommentInPostException(Throwable cause) {
        super(cause);
    }

    protected NotExistCommentInPostException(String message, Throwable cause, boolean enableSuppression, boolean writableStackTrace) {
        super(message, cause, enableSuppression, writableStackTrace);
    }
}
