package com.nameless.spin_off.exception.security;

import com.nameless.spin_off.enums.ErrorEnum;
import com.nameless.spin_off.exception.support.CustomRuntimeException;

public class InvalidRefreshTokenException extends CustomRuntimeException {
    public InvalidRefreshTokenException(String message, ErrorEnum errorEnum) {
        super(message, errorEnum);
    }

    public InvalidRefreshTokenException(ErrorEnum errorEnum) {
        super(errorEnum);
    }
}
