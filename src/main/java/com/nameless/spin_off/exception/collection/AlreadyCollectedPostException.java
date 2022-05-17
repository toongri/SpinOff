package com.nameless.spin_off.exception.collection;

import com.nameless.spin_off.enums.ErrorEnum;
import com.nameless.spin_off.exception.support.CustomRuntimeException;

public class AlreadyCollectedPostException extends CustomRuntimeException {

    public AlreadyCollectedPostException(String message, ErrorEnum errorEnum) {
        super(message, errorEnum);
    }

    public AlreadyCollectedPostException(ErrorEnum errorEnum) {
        super(errorEnum);
    }
}
