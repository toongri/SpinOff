package com.nameless.spin_off.exception.sign;

import com.nameless.spin_off.enums.ErrorEnum;
import com.nameless.spin_off.exception.support.CustomRuntimeException;

public class NotExistEmailLinkageTokenException extends CustomRuntimeException {
    public NotExistEmailLinkageTokenException(String message, ErrorEnum errorEnum) {
        super(message, errorEnum);
    }

    public NotExistEmailLinkageTokenException(ErrorEnum errorEnum) {
        super(errorEnum);
    }
}
