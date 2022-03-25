package com.nameless.spin_off.exception.collection;

import com.nameless.spin_off.entity.enums.ErrorEnum;
import com.nameless.spin_off.exception.support.CustomRuntimeException;

public class IncorrectTitleOfCollectionException extends CustomRuntimeException {
    public IncorrectTitleOfCollectionException(String message, ErrorEnum errorEnum) {
        super(message, errorEnum);
    }

    public IncorrectTitleOfCollectionException(ErrorEnum errorEnum) {
        super(errorEnum);
    }
}
