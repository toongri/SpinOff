package com.nameless.spin_off.exception.comment;

import com.nameless.spin_off.enums.ErrorEnum;
import com.nameless.spin_off.exception.support.CustomRuntimeException;

public class AlreadyLikedCommentInCollectionException extends CustomRuntimeException {
    public AlreadyLikedCommentInCollectionException(String message, ErrorEnum errorEnum) {
        super(message, errorEnum);
    }

    public AlreadyLikedCommentInCollectionException(ErrorEnum errorEnum) {
        super(errorEnum);
    }
}
