package com.nameless.spin_off.exception.comment;

import com.nameless.spin_off.enums.ErrorEnum;
import com.nameless.spin_off.exception.support.CustomRuntimeException;

public class NotExistCommentInPostException extends CustomRuntimeException {
    public NotExistCommentInPostException(String message, ErrorEnum errorEnum) {
        super(message, errorEnum);
    }

    public NotExistCommentInPostException(ErrorEnum errorEnum) {
        super(errorEnum);
    }
}
