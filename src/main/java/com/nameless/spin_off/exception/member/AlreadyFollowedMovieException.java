package com.nameless.spin_off.exception.member;

import com.nameless.spin_off.entity.enums.ErrorEnum;
import com.nameless.spin_off.exception.support.CustomRuntimeException;

public class AlreadyFollowedMovieException extends CustomRuntimeException {
    public AlreadyFollowedMovieException(String message, ErrorEnum errorEnum) {
        super(message, errorEnum);
    }

    public AlreadyFollowedMovieException(ErrorEnum errorEnum) {
        super(errorEnum);
    }
}
