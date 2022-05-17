package com.nameless.spin_off.exception.collection;

import com.nameless.spin_off.enums.ErrorEnum;
import com.nameless.spin_off.exception.support.CustomRuntimeException;

public class AlreadyFollowedCollectionException extends CustomRuntimeException {
    public AlreadyFollowedCollectionException(String message, ErrorEnum errorEnum) {
        super(message, errorEnum);
    }

    public AlreadyFollowedCollectionException(ErrorEnum errorEnum) {
        super(errorEnum);
    }
}
