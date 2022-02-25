package com.nameless.spin_off.entity.enums.post;

import java.util.List;

public enum PostPublicEnum {
    DEFAULT_POST_PUBLIC(List.of(PublicOfPostStatus.A)),
    FOLLOW_POST_PUBLIC(List.of(PublicOfPostStatus.A, PublicOfPostStatus.C));

    private final List<PublicOfPostStatus> privacyBound;

    PostPublicEnum(List<PublicOfPostStatus> privacyBound) {
        this.privacyBound = privacyBound;
    }

    public List<PublicOfPostStatus> getPrivacyBound() {
        return privacyBound;
    }
}
