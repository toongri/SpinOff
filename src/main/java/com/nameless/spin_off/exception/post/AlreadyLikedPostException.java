package com.nameless.spin_off.exception.post;

import com.nameless.spin_off.entity.enums.ErrorEnum;
import com.nameless.spin_off.exception.support.CustomRuntimeException;

public class AlreadyLikedPostException extends CustomRuntimeException {
    public AlreadyLikedPostException(String message, ErrorEnum errorEnum) {
        super(message, errorEnum);
    }

    public AlreadyLikedPostException(ErrorEnum errorEnum) {
        super(errorEnum);
    }
}
