package com.nameless.spin_off.exception.sign;

import com.nameless.spin_off.entity.enums.ErrorEnum;
import com.nameless.spin_off.exception.support.CustomRuntimeException;

public class InCorrectAccountPwException extends CustomRuntimeException {
    public InCorrectAccountPwException(String message, ErrorEnum errorEnum) {
        super(message, errorEnum);
    }

    public InCorrectAccountPwException(ErrorEnum errorEnum) {
        super(errorEnum);
    }
}
