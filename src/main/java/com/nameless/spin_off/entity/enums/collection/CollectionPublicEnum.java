package com.nameless.spin_off.entity.enums.collection;

import java.util.List;

public enum CollectionPublicEnum {
    DEFAULT_COLLECTION_PUBLIC(List.of(PublicOfCollectionStatus.PUBLIC)),
    FOLLOW_COLLECTION_PUBLIC(List.of(PublicOfCollectionStatus.PUBLIC, PublicOfCollectionStatus.FOLLOWER));

    private final List<PublicOfCollectionStatus> privacyBound;

    CollectionPublicEnum(List<PublicOfCollectionStatus> privacyBound) {
        this.privacyBound = privacyBound;
    }

    public List<PublicOfCollectionStatus> getPrivacyBound() {
        return privacyBound;
    }
}
