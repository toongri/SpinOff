package com.nameless.spin_off;

import com.nameless.spin_off.entity.collections.PublicOfCollectionStatus;
import com.nameless.spin_off.entity.post.PublicOfPostStatus;

import java.time.LocalDateTime;
import java.util.List;

public final class StaticVariable {
    public static final Long VIEWED_BY_IP_TIME = 1L;
    public static final Long POPULARITY_DATE_DURATION = 2L;
    public static final int COLLECTION_THUMBNAIL_NUMBER = 4;
    public static final List<PublicOfPostStatus> DEFAULT_POST_PUBLIC =
            List.of(PublicOfPostStatus.PUBLIC);
    public static final List<PublicOfCollectionStatus> DEFAULT_COLLECTION_PUBLIC =
            List.of(PublicOfCollectionStatus.PUBLIC);
    public static final List<PublicOfPostStatus> FOLLOW_POST_PUBLIC =
            List.of(PublicOfPostStatus.PUBLIC, PublicOfPostStatus.FOLLOWER);
    public static final List<PublicOfCollectionStatus> FOLLOW_COLLECTION_PUBLIC =
            List.of(PublicOfCollectionStatus.PUBLIC, PublicOfCollectionStatus.FOLLOWER);
    public static final List<String> CANT_CONTAIN_AT_HASHTAG =
            List.of(" ", "\n");
}
