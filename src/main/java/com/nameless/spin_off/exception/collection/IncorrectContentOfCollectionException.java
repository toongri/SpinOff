package com.nameless.spin_off.exception.collection;

import com.nameless.spin_off.entity.enums.ErrorEnum;
import com.nameless.spin_off.exception.support.CustomRuntimeException;

public class IncorrectContentOfCollectionException extends CustomRuntimeException {
    public IncorrectContentOfCollectionException(String message, ErrorEnum errorEnum) {
        super(message, errorEnum);
    }

    public IncorrectContentOfCollectionException(ErrorEnum errorEnum) {
        super(errorEnum);
    }
}
