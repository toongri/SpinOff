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

    @Override
    public void save(Post post) {
        em.persist(post);
    }

    @Override
    public Post findOne(Long id) {
        return em.find(Post.class, id);
    }

    @Override
    public List<Post> findAll() {

        List<Post> posts = jpaQueryFactory
                .select(post)
                .from(post)
                .join(post.comments, comment)
                .join(post.postLikes, postLike)
                .join(post.medias, media)
                .join(post.postedHashTags, postedHashTag)
                .fetch();

        return posts;

    }

    @Override
    public List<Post> findByTitle(String title) {

        List<Post> posts = jpaQueryFactory
                .select(post)
                .from(post)
                .join(post.comments, comment)
                .join(post.postLikes, postLike)
                .join(post.medias, media)
                .join(post.postedHashTags, postedHashTag)
                .where(post.title.eq(title))
                .fetch();

        return posts;
    }
}
