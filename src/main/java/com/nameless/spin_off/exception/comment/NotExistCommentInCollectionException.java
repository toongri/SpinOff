package com.nameless.spin_off.exception.comment;

import com.nameless.spin_off.entity.enums.ErrorEnum;
import com.nameless.spin_off.exception.support.CustomRuntimeException;

public class NotExistCommentInCollectionException extends CustomRuntimeException {
    public NotExistCommentInCollectionException(String message, ErrorEnum errorEnum) {
        super(message, errorEnum);
    }

    public NotExistCommentInCollectionException(ErrorEnum errorEnum) {
        super(errorEnum);
    }
}
