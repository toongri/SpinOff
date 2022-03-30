package com.nameless.spin_off.exception.sign;

import com.nameless.spin_off.entity.enums.ErrorEnum;
import com.nameless.spin_off.exception.support.CustomRuntimeException;

public class IncorrectNicknameException extends CustomRuntimeException {
    public IncorrectNicknameException(String message, ErrorEnum errorEnum) {
        super(message, errorEnum);
    }

    public IncorrectNicknameException(ErrorEnum errorEnum) {
        super(errorEnum);
    }
}
