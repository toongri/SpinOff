package com.nameless.spin_off.enums;

import java.time.LocalDate;
import java.time.LocalDateTime;

public enum ContentsTimeEnum {
    MEMBER_DELETE_MONTH(1L),
    VIEWED_BY_IP_MINUTE(60L),
    EMAIL_AUTH_MINUTE(3L),
    REGISTER_EMAIL_AUTH_MINUTE(30L);

    private final long time;

    ContentsTimeEnum(long time) {
        this.time = time;
    }

    public long getTime() {
        return time;
    }
    public LocalDateTime getDateTimeMinusMinutes() {
        return LocalDateTime.now().minusMinutes(time);
    }
    public LocalDate getDatePlusMonths() {
        return LocalDate.now().plusMonths(time);
    }
}
