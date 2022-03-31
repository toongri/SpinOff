package com.nameless.spin_off.repository.query;

import com.nameless.spin_off.entity.comment.CommentInCollection;
import com.nameless.spin_off.entity.enums.member.BlockedMemberStatus;
import com.nameless.spin_off.entity.member.QBlockedMember;
import com.nameless.spin_off.repository.support.Querydsl4RepositorySupport;
import org.springframework.stereotype.Repository;

import java.util.Optional;

import static com.nameless.spin_off.entity.comment.QCommentInCollection.commentInCollection;
import static com.nameless.spin_off.entity.comment.QLikedCommentInCollection.likedCommentInCollection;
import static com.nameless.spin_off.entity.member.QBlockedMember.blockedMember;
import static com.nameless.spin_off.entity.member.QMember.member;

@Repository
public class CommentInCollectionQueryRepository extends Querydsl4RepositorySupport {
    public CommentInCollectionQueryRepository() {
        super(CommentInCollection.class);
    }

    public Boolean isBlockMembersComment(Long memberId, Long commentId) {
        QBlockedMember blockingMember = new QBlockedMember("blockingMember");

        Integer fetchOne = getQueryFactory()
                .selectOne()
                .from(commentInCollection)
                .join(commentInCollection.member, member)
                .leftJoin(member.blockedMembers, blockedMember)
                .leftJoin(member.blockingMembers, blockingMember)
                .where(
                        commentInCollection.id.eq(commentId).and(
                                ((blockedMember.member.id.eq(memberId).and(
                                        blockedMember.blockedMemberStatus.eq(BlockedMemberStatus.A))).or(
                                        blockingMember.blockingMember.id.eq(memberId).and(
                                                blockingMember.blockedMemberStatus.eq(BlockedMemberStatus.A))))))
                .fetchFirst();

        return fetchOne != null;
    }

    public Optional<Long> getCommentOwnerId(Long commentId) {
        return Optional.ofNullable(getQueryFactory()
                .select(commentInCollection.member.id)
                .from(commentInCollection)
                .where(
                        commentInCollection.id.eq(commentId))
                .fetchFirst());
    }

    public Boolean isExist(Long id) {
        Integer fetchOne = getQueryFactory()
                .selectOne()
                .from(commentInCollection)
                .where(commentInCollection.id.eq(id))
                .fetchFirst();

        return fetchOne != null;
    }

    public Boolean isExistInCollection(Long id, Long collectionId) {
        Integer fetchOne = getQueryFactory()
                .selectOne()
                .from(commentInCollection)
                .where(
                        commentInCollection.id.eq(id),
                        commentInCollection.collection.id.eq(collectionId))
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
