package com.nameless.spin_off.repository.query;

import com.nameless.spin_off.entity.comment.CommentInCollection;
import com.nameless.spin_off.repository.support.Querydsl4RepositorySupport;
import org.springframework.stereotype.Repository;

import static com.nameless.spin_off.entity.comment.QCommentInCollection.commentInCollection;
import static com.nameless.spin_off.entity.comment.QLikedCommentInCollection.likedCommentInCollection;

@Repository
public class CommentInCollectionQueryRepository extends Querydsl4RepositorySupport {
    public CommentInCollectionQueryRepository() {
        super(CommentInCollection.class);
    }

    public Long getCommentOwnerId(Long commentId) {
        return getQueryFactory()
                .select(commentInCollection.member.id)
                .from(commentInCollection)
                .where(
                        commentInCollection.id.eq(commentId))
                .fetchFirst();
    }

    public Boolean isExist(Long id) {
        Integer fetchOne = getQueryFactory()
                .selectOne()
                .from(commentInCollection)
                .where(commentInCollection.id.eq(id))
                .fetchFirst();

        return fetchOne != null;
    }

    public Boolean isExistLikedCommentInCollection(Long memberId, Long commentId) {
        Integer fetchOne = getQueryFactory()
                .selectOne()
                .from(likedCommentInCollection)
                .where(
                        likedCommentInCollection.member.id.eq(memberId),
                        likedCommentInCollection.commentInCollection.id.eq(commentId))
                .fetchFirst();

        return fetchOne != null;
    }
}