package com.nameless.spin_off.exception.help;

import com.nameless.spin_off.entity.enums.ErrorEnum;
import com.nameless.spin_off.exception.support.CustomRuntimeException;

public class UnknownContentTypeException extends CustomRuntimeException {
    public UnknownContentTypeException(String message, ErrorEnum errorEnum) {
        super(message, errorEnum);
    }

    public UnknownContentTypeException(ErrorEnum errorEnum) {
        super(errorEnum);
    }
}
