package com.nameless.spin_off.exception.member;

import com.nameless.spin_off.enums.ErrorEnum;
import com.nameless.spin_off.exception.support.CustomRuntimeException;

public class NotExistMemberException extends CustomRuntimeException {
    public NotExistMemberException(String message, ErrorEnum errorEnum) {
        super(message, errorEnum);
    }

    public NotExistMemberException(ErrorEnum errorEnum) {
        super(errorEnum);
    }
}
