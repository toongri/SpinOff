package com.nameless.spin_off.repository.query;

import com.nameless.spin_off.entity.comment.CommentInPost;
import com.nameless.spin_off.repository.support.Querydsl4RepositorySupport;
import org.springframework.stereotype.Repository;

import static com.nameless.spin_off.entity.comment.QCommentInPost.commentInPost;
import static com.nameless.spin_off.entity.comment.QLikedCommentInPost.likedCommentInPost;

@Repository
public class CommentInPostQueryRepository extends Querydsl4RepositorySupport {

    public CommentInPostQueryRepository() {
        super(CommentInPost.class);
    }

    public Long getCommentOwnerId(Long commentId) {
        return getQueryFactory()
                .select(commentInPost.member.id)
                .from(commentInPost)
                .where(
                        commentInPost.id.eq(commentId))
                .fetchFirst();
    }

    public Boolean isExist(Long id) {
        Integer fetchOne = getQueryFactory()
                .selectOne()
                .from(commentInPost)
                .where(commentInPost.id.eq(id))
                .fetchFirst();

        return fetchOne != null;
    }

    public Boolean isExistLikedCommentInPost(Long memberId, Long commentId) {
        Integer fetchOne = getQueryFactory()
                .selectOne()
                .from(likedCommentInPost)
                .where(
                        likedCommentInPost.member.id.eq(memberId),
                        likedCommentInPost.commentInPost.id.eq(commentId))
                .fetchFirst();

        return fetchOne != null;
    }
}
