package com.nameless.spin_off.exception.collection;

import com.nameless.spin_off.enums.ErrorEnum;
import com.nameless.spin_off.exception.support.CustomRuntimeException;

public class NotMatchCollectionException extends CustomRuntimeException {
    public NotMatchCollectionException(String message, ErrorEnum errorEnum) {
        super(message, errorEnum);
    }

    public NotMatchCollectionException(ErrorEnum errorEnum) {
        super(errorEnum);
    }
}
