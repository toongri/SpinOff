package com.nameless.spin_off.exception.support;

import com.nameless.spin_off.enums.ErrorEnum;

public class CustomRuntimeForbiddenException extends RuntimeException{

    private final ErrorEnum errorEnum;

    public CustomRuntimeForbiddenException(String message, ErrorEnum errorEnum) {
        super(message);
        this.errorEnum = errorEnum;
    }

    public CustomRuntimeForbiddenException(ErrorEnum errorEnum) {
        super(errorEnum.getMessage());
        this.errorEnum = errorEnum;
    }

    public ErrorEnum getErrorEnum() {
        return errorEnum;
    }
}
