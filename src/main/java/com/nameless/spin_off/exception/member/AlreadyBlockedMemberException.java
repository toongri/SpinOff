package com.nameless.spin_off.exception.member;

import com.nameless.spin_off.enums.ErrorEnum;
import com.nameless.spin_off.exception.support.CustomRuntimeException;

public class AlreadyBlockedMemberException extends CustomRuntimeException {
    public AlreadyBlockedMemberException(String message, ErrorEnum errorEnum) {
        super(message, errorEnum);
    }

    public AlreadyBlockedMemberException(ErrorEnum errorEnum) {
        super(errorEnum);
    }
}
