package com.nameless.spin_off.exception.sign;

import com.nameless.spin_off.entity.enums.ErrorEnum;
import com.nameless.spin_off.exception.support.CustomRuntimeException;

public class IncorrectAccountIdException extends CustomRuntimeException {
    public IncorrectAccountIdException(String message, ErrorEnum errorEnum) {
        super(message, errorEnum);
    }

    public IncorrectAccountIdException(ErrorEnum errorEnum) {
        super(errorEnum);
    }
}
