package com.nameless.spin_off.exception.member;

import com.nameless.spin_off.enums.ErrorEnum;
import com.nameless.spin_off.exception.support.CustomRuntimeException;

public class AlreadyRegisterEmailException extends CustomRuntimeException {
    public AlreadyRegisterEmailException(String message, ErrorEnum errorEnum) {
        super(message, errorEnum);
    }

    public AlreadyRegisterEmailException(ErrorEnum errorEnum) {
        super(errorEnum);
    }
}
