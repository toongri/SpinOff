package com.nameless.spin_off.exception.sign;

import com.nameless.spin_off.entity.enums.ErrorEnum;
import com.nameless.spin_off.exception.support.CustomRuntimeException;

public class NotMatchAccountPwException extends CustomRuntimeException {
    public NotMatchAccountPwException(String message, ErrorEnum errorEnum) {
        super(message, errorEnum);
    }

    public NotMatchAccountPwException(ErrorEnum errorEnum) {
        super(errorEnum);
    }
}
