package com.nameless.spin_off.exception.sign;

import com.nameless.spin_off.enums.ErrorEnum;
import com.nameless.spin_off.exception.support.CustomRuntimeException;

public class IncorrectProviderException extends CustomRuntimeException {
    public IncorrectProviderException(String message, ErrorEnum errorEnum) {
        super(message, errorEnum);
    }

    public IncorrectProviderException(ErrorEnum errorEnum) {
        super(errorEnum);
    }
}
