package com.nameless.spin_off.exception.post;

import com.nameless.spin_off.enums.ErrorEnum;
import com.nameless.spin_off.exception.support.CustomRuntimeException;

public class AlreadyPostedHashtagException extends CustomRuntimeException {
    public AlreadyPostedHashtagException(String message, ErrorEnum errorEnum) {
        super(message, errorEnum);
    }

    public AlreadyPostedHashtagException(ErrorEnum errorEnum) {
        super(errorEnum);
    }
}
