package com.nameless.spin_off.exception.post;

import com.nameless.spin_off.enums.ErrorEnum;
import com.nameless.spin_off.exception.support.CustomRuntimeException;

public class IncorrectTitleOfPostException extends CustomRuntimeException {
    public IncorrectTitleOfPostException(String message, ErrorEnum errorEnum) {
        super(message, errorEnum);
    }

    public IncorrectTitleOfPostException(ErrorEnum errorEnum) {
        super(errorEnum);
    }
}
