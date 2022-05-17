package com.nameless.spin_off.exception.sign;

import com.nameless.spin_off.enums.ErrorEnum;
import com.nameless.spin_off.exception.support.CustomRuntimeException;

public class NotExistAccountIdException extends CustomRuntimeException {
    public NotExistAccountIdException(String message, ErrorEnum errorEnum) {
        super(message, errorEnum);
    }

    public NotExistAccountIdException(ErrorEnum errorEnum) {
        super(errorEnum);
    }
}
