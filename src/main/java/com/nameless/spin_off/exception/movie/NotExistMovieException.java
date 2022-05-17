package com.nameless.spin_off.exception.movie;

import com.nameless.spin_off.enums.ErrorEnum;
import com.nameless.spin_off.exception.support.CustomRuntimeException;

public class NotExistMovieException extends CustomRuntimeException {
    public NotExistMovieException(String message, ErrorEnum errorEnum) {
        super(message, errorEnum);
    }

    public NotExistMovieException(ErrorEnum errorEnum) {
        super(errorEnum);
    }
}
