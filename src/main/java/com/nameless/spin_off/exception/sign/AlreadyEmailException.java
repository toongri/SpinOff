package com.nameless.spin_off.exception.sign;

import com.nameless.spin_off.enums.ErrorEnum;
import com.nameless.spin_off.exception.support.CustomRuntimeException;

public class AlreadyEmailException extends CustomRuntimeException {
    public AlreadyEmailException(String message, ErrorEnum errorEnum) {
        super(message, errorEnum);
    }

    public AlreadyEmailException(ErrorEnum errorEnum) {
        super(errorEnum);
    }
}
