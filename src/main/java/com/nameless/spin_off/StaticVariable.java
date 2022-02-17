package com.nameless.spin_off;

import com.nameless.spin_off.entity.collections.PublicOfCollectionStatus;
import com.nameless.spin_off.entity.post.PublicOfPostStatus;

import java.time.LocalDateTime;
import java.util.List;

public final class StaticVariable {
    public static final Long VIEWED_BY_IP_MINUTE = 60L;
//    public static final Long VIEWED_BY_IP_MINUTE = 0L;

    public static final List<Long> COLLECTION_VIEW_COUNT_DAYS = List.of(0L, 7L, 14L, 21L, 28L);
    public static final List<Double> COLLECTION_VIEW_COUNT_SCORES = List.of(1.0, 0.5, 0.3, 0.1);
    public static final List<Long> COLLECTION_LIKE_COUNT_DAYS = List.of(0L, 7L, 14L, 21L, 28L);
    public static final List<Double> COLLECTION_LIKE_COUNT_SCORES = List.of(1.0, 0.5, 0.3, 0.1);
    public static final List<Long> COLLECTION_COMMENT_COUNT_DAYS = List.of(0L, 7L, 14L, 21L, 28L);
    public static final List<Double> COLLECTION_COMMENT_COUNT_SCORES = List.of(1.0, 0.5, 0.3, 0.1);
    public static final List<Long> COLLECTION_FOLLOW_COUNT_DAYS = List.of(0L, 7L, 14L, 21L, 28L);
    public static final List<Double> COLLECTION_FOLLOW_COUNT_SCORES = List.of(1.0, 0.5, 0.3, 0.1);


    public static final List<Long> POST_VIEW_COUNT_DAYS = List.of(0L, 7L, 14L, 21L, 28L);
    public static final List<Double> POST_VIEW_COUNT_SCORES = List.of(1.0, 0.5, 0.3, 0.1);
    public static final List<Long> POST_LIKE_COUNT_DAYS = List.of(0L, 7L, 14L, 21L, 28L);
    public static final List<Double> POST_LIKE_COUNT_SCORES = List.of(1.0, 0.5, 0.3, 0.1);
    public static final List<Long> POST_COMMENT_COUNT_DAYS = List.of(0L, 7L, 14L, 21L, 28L);
    public static final List<Double> POST_COMMENT_COUNT_SCORES = List.of(1.0, 0.5, 0.3, 0.1);
    public static final List<Long> POST_COLLECT_COUNT_DAYS = List.of(0L, 7L, 14L, 21L, 28L);
    public static final List<Double> POST_COLLECT_COUNT_SCORES = List.of(1.0, 0.5, 0.3, 0.1);


    public static final List<Long> MOVIE_VIEW_COUNT_DAYS = List.of(0L, 7L, 14L, 21L, 28L);
    public static final List<Double> MOVIE_VIEW_COUNT_SCORES = List.of(1.0, 0.5, 0.3, 0.1);
    public static final List<Long> MOVIE_POST_COUNT_DAYS = List.of(0L, 7L, 14L, 21L, 28L);
    public static final List<Double> MOVIE_POST_COUNT_SCORES = List.of(1.0, 0.5, 0.3, 0.1);
    public static final List<Long> MOVIE_FOLLOW_COUNT_DAYS = List.of(0L, 7L, 14L, 21L, 28L);
    public static final List<Double> MOVIE_FOLLOW_COUNT_SCORES = List.of(1.0, 0.5, 0.3, 0.1);


    public static final List<Long> HASHTAG_VIEW_COUNT_DAYS = List.of(0L, 7L, 14L, 21L, 28L);
    public static final List<Double> HASHTAG_VIEW_COUNT_SCORES = List.of(1.0, 0.5, 0.3, 0.1);
    public static final List<Long> HASHTAG_POST_COUNT_DAYS = List.of(0L, 7L, 14L, 21L, 28L);
    public static final List<Double> HASHTAG_POST_COUNT_SCORES = List.of(1.0, 0.5, 0.3, 0.1);
    public static final List<Long> HASHTAG_FOLLOW_COUNT_DAYS = List.of(0L, 7L, 14L, 21L, 28L);
    public static final List<Double> HASHTAG_FOLLOW_COUNT_SCORES = List.of(1.0, 0.5, 0.3, 0.1);

    public static final List<Long> MEMBER_FOLLOW_COUNT_DAYS = List.of(0L, 7L, 14L, 21L, 28L);
    public static final List<Double> MEMBER_FOLLOW_COUNT_SCORES = List.of(1.0, 0.5, 0.3, 0.1);

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
