package com.nameless.spin_off.exception.post;

import com.nameless.spin_off.enums.ErrorEnum;
import com.nameless.spin_off.exception.support.CustomRuntimeException;

public class NotExistPostException extends CustomRuntimeException {
    public NotExistPostException(String message, ErrorEnum errorEnum) {
        super(message, errorEnum);
    }

    public NotExistPostException(ErrorEnum errorEnum) {
        super(errorEnum);
    }
}
