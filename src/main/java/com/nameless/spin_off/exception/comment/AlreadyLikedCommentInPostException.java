package com.nameless.spin_off.exception.comment;

import com.nameless.spin_off.enums.ErrorEnum;
import com.nameless.spin_off.exception.support.CustomRuntimeException;

public class AlreadyLikedCommentInPostException extends CustomRuntimeException {
    public AlreadyLikedCommentInPostException(String message, ErrorEnum errorEnum) {
        super(message, errorEnum);
    }

    public AlreadyLikedCommentInPostException(ErrorEnum errorEnum) {
        super(errorEnum);
    }
}
