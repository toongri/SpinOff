package com.nameless.spin_off.exception.collection;

import com.nameless.spin_off.enums.ErrorEnum;
import com.nameless.spin_off.exception.support.CustomRuntimeException;

public class AlreadyLikedCollectionException extends CustomRuntimeException {
    public AlreadyLikedCollectionException(String message, ErrorEnum errorEnum) {
        super(message, errorEnum);
    }

    public AlreadyLikedCollectionException(ErrorEnum errorEnum) {
        super(errorEnum);
    }
}
