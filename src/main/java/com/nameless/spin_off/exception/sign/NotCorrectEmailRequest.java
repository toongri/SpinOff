package com.nameless.spin_off.exception.sign;

import com.nameless.spin_off.entity.enums.ErrorEnum;
import com.nameless.spin_off.exception.support.CustomRuntimeException;

public class NotCorrectEmailRequest extends CustomRuntimeException {
    public NotCorrectEmailRequest(String message, ErrorEnum errorEnum) {
        super(message, errorEnum);
    }

    public NotCorrectEmailRequest(ErrorEnum errorEnum) {
        super(errorEnum);
    }
}
