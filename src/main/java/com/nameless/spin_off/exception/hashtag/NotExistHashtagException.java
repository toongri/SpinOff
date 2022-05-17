package com.nameless.spin_off.exception.hashtag;

import com.nameless.spin_off.enums.ErrorEnum;
import com.nameless.spin_off.exception.support.CustomRuntimeException;

public class NotExistHashtagException extends CustomRuntimeException {
    public NotExistHashtagException(String message, ErrorEnum errorEnum) {
        super(message, errorEnum);
    }

    public NotExistHashtagException(ErrorEnum errorEnum) {
        super(errorEnum);
    }
}
