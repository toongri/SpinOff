package com.nameless.spin_off.exception.security;

import com.nameless.spin_off.entity.enums.ErrorEnum;
import com.nameless.spin_off.exception.support.CustomRuntimeForbiddenException;

public class DontHaveAuthorityException extends CustomRuntimeForbiddenException {
    public DontHaveAuthorityException(String message, ErrorEnum errorEnum) {
        super(message, errorEnum);
    }

    public DontHaveAuthorityException(ErrorEnum errorEnum) {
        super(errorEnum);
    }
}
