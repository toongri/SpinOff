package com.nameless.spin_off.exception.member;

import com.nameless.spin_off.enums.ErrorEnum;
import com.nameless.spin_off.exception.support.CustomRuntimeException;

public class AlreadyComplainException extends CustomRuntimeException {
    public AlreadyComplainException(String message, ErrorEnum errorEnum) {
        super(message, errorEnum);
    }

    public AlreadyComplainException(ErrorEnum errorEnum) {
        super(errorEnum);
    }
}
