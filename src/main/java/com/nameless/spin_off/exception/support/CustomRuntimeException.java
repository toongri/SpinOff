package com.nameless.spin_off.exception.support;

import com.nameless.spin_off.entity.enums.ErrorEnum;

public class CustomRuntimeException extends RuntimeException{

    private final ErrorEnum errorEnum;

    public CustomRuntimeException(String message, ErrorEnum errorEnum) {
        super(message);
        this.errorEnum = errorEnum;
    }

    public CustomRuntimeException(ErrorEnum errorEnum) {
        super(errorEnum.getMessage());
        this.errorEnum = errorEnum;
    }

    public ErrorEnum getErrorEnum() {
        return errorEnum;
    }
}
