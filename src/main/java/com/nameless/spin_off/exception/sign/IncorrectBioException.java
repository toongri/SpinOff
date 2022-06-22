package com.nameless.spin_off.exception.sign;

import com.nameless.spin_off.enums.ErrorEnum;
import com.nameless.spin_off.exception.support.CustomRuntimeException;

public class IncorrectBioException extends CustomRuntimeException {
    public IncorrectBioException() {
    }

    public IncorrectBioException(ErrorEnum errorEnum) {
        super(errorEnum);
    }

    public IncorrectBioException(String message, ErrorEnum errorEnum) {
        super(message, errorEnum);
    }
}
