package com.nameless.spin_off.exception.sign;

import com.nameless.spin_off.entity.enums.ErrorEnum;
import com.nameless.spin_off.exception.support.CustomRuntimeException;

public class NotExistEmailAuthTokenException extends CustomRuntimeException {
    public NotExistEmailAuthTokenException(String message, ErrorEnum errorEnum) {
        super(message, errorEnum);
    }

    public NotExistEmailAuthTokenException(ErrorEnum errorEnum) {
        super(errorEnum);
    }
}
