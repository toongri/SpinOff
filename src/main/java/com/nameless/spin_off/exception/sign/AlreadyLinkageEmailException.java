package com.nameless.spin_off.exception.sign;

import com.nameless.spin_off.entity.enums.ErrorEnum;
import com.nameless.spin_off.exception.support.CustomRuntimeException;

public class AlreadyLinkageEmailException extends CustomRuntimeException {
    public AlreadyLinkageEmailException(String message, ErrorEnum errorEnum) {
        super(message, errorEnum);
    }

    public AlreadyLinkageEmailException(ErrorEnum errorEnum) {
        super(errorEnum);
    }
}
