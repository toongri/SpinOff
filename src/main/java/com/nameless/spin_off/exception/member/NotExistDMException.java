package com.nameless.spin_off.exception.member;

import com.nameless.spin_off.entity.enums.ErrorEnum;
import com.nameless.spin_off.exception.support.CustomRuntimeException;

public class NotExistDMException extends CustomRuntimeException {
    public NotExistDMException(String message, ErrorEnum errorEnum) {
        super(message, errorEnum);
    }

    public NotExistDMException(ErrorEnum errorEnum) {
        super(errorEnum);
    }
}
