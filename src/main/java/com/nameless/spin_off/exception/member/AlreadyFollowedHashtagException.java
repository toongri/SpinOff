package com.nameless.spin_off.exception.member;

import com.nameless.spin_off.enums.ErrorEnum;
import com.nameless.spin_off.exception.support.CustomRuntimeException;

public class AlreadyFollowedHashtagException extends CustomRuntimeException {
    public AlreadyFollowedHashtagException(String message, ErrorEnum errorEnum) {
        super(message, errorEnum);
    }

    public AlreadyFollowedHashtagException(ErrorEnum errorEnum) {
        super(errorEnum);
    }
}
