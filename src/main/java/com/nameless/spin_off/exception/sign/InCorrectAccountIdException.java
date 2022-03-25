package com.nameless.spin_off.exception.sign;

import com.nameless.spin_off.entity.enums.ErrorEnum;
import com.nameless.spin_off.exception.support.CustomRuntimeException;

public class InCorrectAccountIdException extends CustomRuntimeException {
    public InCorrectAccountIdException(String message, ErrorEnum errorEnum) {
        super(message, errorEnum);
    }

    public InCorrectAccountIdException(ErrorEnum errorEnum) {
        super(errorEnum);
    }
}
