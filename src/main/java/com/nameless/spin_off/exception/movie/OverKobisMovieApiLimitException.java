package com.nameless.spin_off.exception.movie;

import com.nameless.spin_off.enums.ErrorEnum;
import com.nameless.spin_off.exception.support.CustomRuntimeException;

public class OverKobisMovieApiLimitException extends CustomRuntimeException {
    public OverKobisMovieApiLimitException(String message, ErrorEnum errorEnum) {
        super(message, errorEnum);
    }

    public OverKobisMovieApiLimitException() {
        super(ErrorEnum.OVER_KOBIS_MOVIE_API_LIMIT.getMessage(), ErrorEnum.OVER_KOBIS_MOVIE_API_LIMIT);
    }

    public OverKobisMovieApiLimitException(ErrorEnum errorEnum) {
        super(errorEnum);
    }
}
