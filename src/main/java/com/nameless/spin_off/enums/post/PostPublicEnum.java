package com.nameless.spin_off.enums.post;

import java.util.List;

public enum PostPublicEnum {
    DEFAULT_POST_PUBLIC(List.of(PublicOfPostStatus.A)),
    FOLLOW_POST_PUBLIC(List.of(PublicOfPostStatus.A, PublicOfPostStatus.C)),
    ADMIN_POST_PUBLIC(List.of(PublicOfPostStatus.A, PublicOfPostStatus.B, PublicOfPostStatus.C));

    private final List<PublicOfPostStatus> privacyBound;

    PostPublicEnum(List<PublicOfPostStatus> privacyBound) {
        this.privacyBound = privacyBound;
    }

    public List<PublicOfPostStatus> getPrivacyBound() {
        return privacyBound;
    }
}
