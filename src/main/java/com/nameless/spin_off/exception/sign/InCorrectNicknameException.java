package com.nameless.spin_off.exception.sign;

import com.nameless.spin_off.entity.enums.ErrorEnum;
import com.nameless.spin_off.exception.support.CustomRuntimeException;

public class InCorrectNicknameException extends CustomRuntimeException {
    public InCorrectNicknameException(String message, ErrorEnum errorEnum) {
        super(message, errorEnum);
    }

    public InCorrectNicknameException(ErrorEnum errorEnum) {
        super(errorEnum);
    }
}
