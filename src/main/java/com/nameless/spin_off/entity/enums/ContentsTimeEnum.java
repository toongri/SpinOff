package com.nameless.spin_off.entity.enums;

import java.time.LocalDateTime;

public enum ContentsTimeEnum {
    VIEWED_BY_IP_MINUTE(60L),
    EMAIL_AUTH_MINUTE(5L),
    REGISTER_EMAIL_AUTH_MINUTE(30L);

    private final long time;

    ContentsTimeEnum(long time) {
        this.time = time;
    }

    public long getTime() {
        return time;
    }
    public LocalDateTime getDateTime() {
        return LocalDateTime.now().minusMinutes(time);
    }
}
