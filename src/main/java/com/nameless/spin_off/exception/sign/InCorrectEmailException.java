package com.nameless.spin_off.exception.sign;

import com.nameless.spin_off.entity.enums.ErrorEnum;
import com.nameless.spin_off.exception.support.CustomRuntimeException;

public class InCorrectEmailException extends CustomRuntimeException {
    public InCorrectEmailException(String message, ErrorEnum errorEnum) {
        super(message, errorEnum);
    }

    public InCorrectEmailException(ErrorEnum errorEnum) {
        super(errorEnum);
    }
}
