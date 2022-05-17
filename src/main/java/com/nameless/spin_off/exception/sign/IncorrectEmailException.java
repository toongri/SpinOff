package com.nameless.spin_off.exception.sign;

import com.nameless.spin_off.enums.ErrorEnum;
import com.nameless.spin_off.exception.support.CustomRuntimeException;

public class IncorrectEmailException extends CustomRuntimeException {
    public IncorrectEmailException(String message, ErrorEnum errorEnum) {
        super(message, errorEnum);
    }

    public IncorrectEmailException(ErrorEnum errorEnum) {
        super(errorEnum);
    }
}
