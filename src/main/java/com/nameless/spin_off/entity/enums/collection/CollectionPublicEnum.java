package com.nameless.spin_off.entity.enums.collection;

import java.util.List;

public enum CollectionPublicEnum {
    DEFAULT_COLLECTION_PUBLIC(List.of(PublicOfCollectionStatus.A)),
    FOLLOW_COLLECTION_PUBLIC(List.of(PublicOfCollectionStatus.A, PublicOfCollectionStatus.C)),
    ADMIN_COLLECTION_PUBLIC(List.of(PublicOfCollectionStatus.A, PublicOfCollectionStatus.B, PublicOfCollectionStatus.C));

    private final List<PublicOfCollectionStatus> privacyBound;

    CollectionPublicEnum(List<PublicOfCollectionStatus> privacyBound) {
        this.privacyBound = privacyBound;
    }

    public List<PublicOfCollectionStatus> getPrivacyBound() {
        return privacyBound;
    }
}
