package com.nameless.spin_off.entity.enums;

import java.util.List;

public enum BanListOfContentsEnum {
    CANT_CONTAIN_AT_HASHTAG(List.of(" ", "\n"));

    private final List<String> banList;

    BanListOfContentsEnum(List<String> banList) {
        this.banList = banList;
    }

    public List<String> getBanList() {
        return banList;
    }
}
