package com.nameless.spin_off.exception.sign;

import com.nameless.spin_off.enums.ErrorEnum;
import com.nameless.spin_off.exception.support.CustomRuntimeException;

public class AuthenticationEntryException extends CustomRuntimeException {
    public AuthenticationEntryException(String message, ErrorEnum errorEnum) {
        super(message, errorEnum);
    }

    public AuthenticationEntryException(ErrorEnum errorEnum) {
        super(errorEnum);
    }
}
