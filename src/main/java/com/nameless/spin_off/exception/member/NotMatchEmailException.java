package com.nameless.spin_off.exception.member;

import com.nameless.spin_off.enums.ErrorEnum;
import com.nameless.spin_off.exception.support.CustomRuntimeException;

public class NotMatchEmailException extends CustomRuntimeException {
    public NotMatchEmailException(String message, ErrorEnum errorEnum) {
        super(message, errorEnum);
    }

    public NotMatchEmailException(ErrorEnum errorEnum) {
        super(errorEnum);
    }
}
