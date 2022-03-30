package com.nameless.spin_off.exception.sign;

import com.nameless.spin_off.entity.enums.ErrorEnum;
import com.nameless.spin_off.exception.support.CustomRuntimeException;

public class SocialException extends CustomRuntimeException {
    public SocialException(String message, ErrorEnum errorEnum) {
        super(message, errorEnum);
    }

    public SocialException(ErrorEnum errorEnum) {
        super(errorEnum);
    }
}
