package com.nameless.spin_off.exception.search;

import com.nameless.spin_off.enums.ErrorEnum;
import com.nameless.spin_off.exception.support.CustomRuntimeException;

public class IncorrectLengthRelatedKeywordException extends CustomRuntimeException {
    public IncorrectLengthRelatedKeywordException(String message, ErrorEnum errorEnum) {
        super(message, errorEnum);
    }

    public IncorrectLengthRelatedKeywordException(ErrorEnum errorEnum) {
        super(errorEnum);
    }
}
