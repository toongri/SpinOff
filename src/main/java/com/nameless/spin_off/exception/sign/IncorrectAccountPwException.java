package com.nameless.spin_off.exception.sign;

import com.nameless.spin_off.enums.ErrorEnum;
import com.nameless.spin_off.exception.support.CustomRuntimeException;

public class IncorrectAccountPwException extends CustomRuntimeException {
    public IncorrectAccountPwException(String message, ErrorEnum errorEnum) {
        super(message, errorEnum);
    }

    public IncorrectAccountPwException(ErrorEnum errorEnum) {
        super(errorEnum);
    }
}
