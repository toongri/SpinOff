package com.nameless.spin_off.repository.post;

import com.nameless.spin_off.domain.post.*;
import com.querydsl.jpa.impl.JPAQueryFactory;
import lombok.RequiredArgsConstructor;

import javax.persistence.EntityManager;
import java.util.List;

import static com.nameless.spin_off.domain.post.QComment.comment;
import static com.nameless.spin_off.domain.post.QMedia.media;
import static com.nameless.spin_off.domain.post.QPost.post;
import static com.nameless.spin_off.domain.post.QPostLike.postLike;
import static com.nameless.spin_off.domain.post.QPostedHashTag.postedHashTag;

@RequiredArgsConstructor
public class PostRepositoryImpl implements PostRepositoryCustom{

    private final EntityManager em;
    private final JPAQueryFactory jpaQueryFactory;

}
