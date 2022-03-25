package com.nameless.spin_off.exception.member;

import com.nameless.spin_off.entity.enums.ErrorEnum;
import com.nameless.spin_off.exception.support.CustomRuntimeException;

public class AlreadyFollowedMemberException extends CustomRuntimeException {
    public AlreadyFollowedMemberException(String message, ErrorEnum errorEnum) {
        super(message, errorEnum);
    }

    public AlreadyFollowedMemberException(ErrorEnum errorEnum) {
        super(errorEnum);
    }
}
