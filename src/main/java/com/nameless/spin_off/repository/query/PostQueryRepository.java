package com.nameless.spin_off.repository.query;

import com.nameless.spin_off.entity.post.*;
import com.querydsl.jpa.impl.JPAQueryFactory;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

import java.util.List;

import static com.nameless.spin_off.entity.member.QMember.member;
import static com.nameless.spin_off.entity.post.QComment.comment;
import static com.nameless.spin_off.entity.post.QMedia.media;
import static com.nameless.spin_off.entity.post.QPost.post;
import static com.nameless.spin_off.entity.post.QPostLike.postLike;
import static com.nameless.spin_off.entity.post.QPostedHashTag.postedHashTag;

@Repository
@RequiredArgsConstructor
public class PostQueryRepository {

    private final JPAQueryFactory jpaQueryFactory;

    public Post findOne(Long id) {
        return jpaQueryFactory
                .select(post)
                .from(post)
                .leftJoin(post.member, member).fetchJoin()
                .leftJoin(post.comments, comment)
                .leftJoin(post.postLikes, postLike)
                .leftJoin(post.postedHashTags, postedHashTag)
                .leftJoin(post.medias, media)
                .where(post.id.eq(id))
                .fetchOne();
    }

    public List<Post> findAll() {
        return jpaQueryFactory
                .select(post)
                .from(post)
                .leftJoin(post.member, member).fetchJoin()
                .leftJoin(post.comments, comment)
                .leftJoin(post.postLikes, postLike)
                .leftJoin(post.postedHashTags, postedHashTag)
                .leftJoin(post.medias, media)
                .fetch();
    }
}
