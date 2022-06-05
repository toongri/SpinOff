package com.nameless.spin_off.exception.sign;

import com.nameless.spin_off.enums.ErrorEnum;
import com.nameless.spin_off.exception.support.CustomRuntimeException;

public class IncorrectPhoneNumberException extends CustomRuntimeException {
    public IncorrectPhoneNumberException(String message, ErrorEnum errorEnum) {
        super(message, errorEnum);
    }

    public IncorrectPhoneNumberException(ErrorEnum errorEnum) {
        super(errorEnum);
    }
}
