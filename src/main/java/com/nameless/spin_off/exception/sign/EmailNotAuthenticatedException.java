package com.nameless.spin_off.exception.sign;

import com.nameless.spin_off.entity.enums.ErrorEnum;
import com.nameless.spin_off.exception.support.CustomRuntimeException;

public class EmailNotAuthenticatedException extends CustomRuntimeException {
    public EmailNotAuthenticatedException(String message, ErrorEnum errorEnum) {
        super(message, errorEnum);
    }

    public EmailNotAuthenticatedException(ErrorEnum errorEnum) {
        super(errorEnum);
    }
}
