package com.nameless.spin_off.exception.collection;

import com.nameless.spin_off.enums.ErrorEnum;
import com.nameless.spin_off.exception.support.CustomRuntimeException;

public class NotExistCollectionException extends CustomRuntimeException {
    public NotExistCollectionException(String message, ErrorEnum errorEnum) {
        super(message, errorEnum);
    }

    public NotExistCollectionException(ErrorEnum errorEnum) {
        super(errorEnum);
    }
}
