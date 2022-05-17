package com.nameless.spin_off.exception.member;

import com.nameless.spin_off.enums.ErrorEnum;
import com.nameless.spin_off.exception.support.CustomRuntimeException;

public class AlreadyAccountIdException extends CustomRuntimeException {
    public AlreadyAccountIdException(String message, ErrorEnum errorEnum) {
        super(message, errorEnum);
    }

    public AlreadyAccountIdException(ErrorEnum errorEnum) {
        super(errorEnum);
    }
}
