package com.nameless.spin_off.exception.post;

import com.nameless.spin_off.enums.ErrorEnum;
import com.nameless.spin_off.exception.support.CustomRuntimeException;

public class IncorrectPostImageLengthException extends CustomRuntimeException {
    public IncorrectPostImageLengthException(String message, ErrorEnum errorEnum) {
        super(message, errorEnum);
    }

    public IncorrectPostImageLengthException(ErrorEnum errorEnum) {
        super(errorEnum);
    }
}
