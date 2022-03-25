package com.nameless.spin_off.exception.sign;

import com.nameless.spin_off.entity.enums.ErrorEnum;
import com.nameless.spin_off.exception.support.CustomRuntimeException;

public class AlreadyNicknameException extends CustomRuntimeException {
    public AlreadyNicknameException(String message, ErrorEnum errorEnum) {
        super(message, errorEnum);
    }

    public AlreadyNicknameException(ErrorEnum errorEnum) {
        super(errorEnum);
    }
}
