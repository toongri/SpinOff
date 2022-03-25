package com.nameless.spin_off.exception.security;

import com.nameless.spin_off.entity.enums.ErrorEnum;
import org.springframework.security.core.AuthenticationException;

public class AlreadyAuthEmailException extends AuthenticationException {

    private final ErrorEnum errorEnum;
    public AlreadyAuthEmailException(ErrorEnum errorEnum) {
        super(errorEnum.getMessage());
        this.errorEnum = errorEnum;
    }
}
