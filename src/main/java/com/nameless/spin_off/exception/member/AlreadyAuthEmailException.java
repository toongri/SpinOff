package com.nameless.spin_off.exception.member;

import org.springframework.security.core.AuthenticationException;

public class AlreadyAuthEmailException extends AuthenticationException {
    public AlreadyAuthEmailException(String msg) {
        super(msg);
    }
}
