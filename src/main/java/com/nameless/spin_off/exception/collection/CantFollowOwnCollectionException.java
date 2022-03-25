package com.nameless.spin_off.exception.collection;

import com.nameless.spin_off.entity.enums.ErrorEnum;
import com.nameless.spin_off.exception.support.CustomRuntimeException;

public class CantFollowOwnCollectionException extends CustomRuntimeException {
    public CantFollowOwnCollectionException(String message, ErrorEnum errorEnum) {
        super(message, errorEnum);
    }

    public CantFollowOwnCollectionException(ErrorEnum errorEnum) {
        super(errorEnum);
    }
}
