package com.nameless.spin_off.exception.post;

import com.nameless.spin_off.entity.enums.ErrorEnum;
import com.nameless.spin_off.exception.support.CustomRuntimeException;

public class IncorrectContentOfPostException extends CustomRuntimeException {
    public IncorrectContentOfPostException(String message, ErrorEnum errorEnum) {
        super(message, errorEnum);
    }

    public IncorrectContentOfPostException(ErrorEnum errorEnum) {
        super(errorEnum);
    }
}
