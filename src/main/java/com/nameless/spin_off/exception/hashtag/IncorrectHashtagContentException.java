package com.nameless.spin_off.exception.hashtag;

import com.nameless.spin_off.entity.enums.ErrorEnum;
import com.nameless.spin_off.exception.support.CustomRuntimeException;

public class IncorrectHashtagContentException extends CustomRuntimeException {
    public IncorrectHashtagContentException(String message, ErrorEnum errorEnum) {
        super(message, errorEnum);
    }

    public IncorrectHashtagContentException(ErrorEnum errorEnum) {
        super(errorEnum);
    }
}
