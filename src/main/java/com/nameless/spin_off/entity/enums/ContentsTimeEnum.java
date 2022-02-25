package com.nameless.spin_off.entity.enums;

public enum ContentsTimeEnum {
    VIEWED_BY_IP_MINUTE(60L);

    private final long time;

    ContentsTimeEnum(long time) {
        this.time = time;
    }

    public long getTime() {
        return time;
    }
}
