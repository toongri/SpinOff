package com.nameless.spin_off.repository.query;

import com.nameless.spin_off.dto.CommentDto.ContentCommentDto;
import com.nameless.spin_off.dto.QCommentDto_ContentCommentDto;
import com.nameless.spin_off.entity.comment.CommentInPost;
import com.nameless.spin_off.entity.enums.member.BlockedMemberStatus;
import com.nameless.spin_off.entity.member.QBlockedMember;
import com.nameless.spin_off.repository.support.Querydsl4RepositorySupport;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

import static com.nameless.spin_off.entity.comment.QCommentInPost.commentInPost;
import static com.nameless.spin_off.entity.comment.QLikedCommentInPost.likedCommentInPost;
import static com.nameless.spin_off.entity.member.QBlockedMember.blockedMember;
import static com.nameless.spin_off.entity.member.QMember.member;
import static com.nameless.spin_off.entity.post.QPost.post;

@Repository
public class CommentInPostQueryRepository extends Querydsl4RepositorySupport {

    public CommentInPostQueryRepository() {
        super(CommentInPost.class);
    }

    public List<Long> findAllLikedCommentIdByMemberId(Long memberId) {
        return getQueryFactory()
                .select(likedCommentInPost.commentInPost.id)
                .from(likedCommentInPost)
                .where(likedCommentInPost.member.id.eq(memberId))
                .fetch();
    }

    public List<ContentCommentDto> findAllByCollectionId(Long postId, List<Long> blockedMemberIds) {
        return getQueryFactory()
                .select(new QCommentDto_ContentCommentDto(
                        commentInPost.id, commentInPost.member.id, commentInPost.member.profileImg,
                        commentInPost.member.nickname, commentInPost.content, commentInPost.createdDate,
                        commentInPost.isDeleted, likedCommentInPost.count(), commentInPost.parent.id))
                .from(commentInPost)
                .join(commentInPost.member, member)
                .leftJoin(commentInPost.likedCommentInPosts, likedCommentInPost)
                .on(likedCommentInPost.member.id.notIn(blockedMemberIds))
                .groupBy(commentInPost)
                .where(commentInPost.post.id.eq(postId))
                .orderBy(commentInPost.id.desc())
                .fetch();
    }

    public Boolean isBlockMembersComment(Long memberId, Long commentId) {
        QBlockedMember blockingMember = new QBlockedMember("blockingMember");

        Integer fetchOne = getQueryFactory()
                .selectOne()
                .from(commentInPost)
                .join(commentInPost.member, member)
                .leftJoin(member.blockedMembers, blockedMember)
                .leftJoin(member.blockingMembers, blockingMember)
                .where(
                        commentInPost.id.eq(commentId).and(
                                ((blockedMember.member.id.eq(memberId).and(
                                        blockedMember.blockedMemberStatus.eq(BlockedMemberStatus.A))).or(
                                        blockingMember.blockingMember.id.eq(memberId).and(
                                                blockingMember.blockedMemberStatus.eq(BlockedMemberStatus.A))))))
                .fetchFirst();

        return fetchOne != null;
    }
    public Optional<Long> findCommentOwnerId(Long commentId) {
        return Optional.ofNullable(getQueryFactory()
                .select(commentInPost.member.id)
                .from(commentInPost)
                .where(
                        commentInPost.id.eq(commentId))
                .fetchFirst());
    }

    public Boolean isExist(Long id) {
        Integer fetchOne = getQueryFactory()
                .selectOne()
                .from(commentInPost)
                .where(commentInPost.id.eq(id))
                .fetchFirst();

        return fetchOne != null;
    }

    public Boolean isExistInPost(Long id, Long postId) {
        Integer fetchOne = getQueryFactory()
                .selectOne()
                .from(commentInPost)
                .join(commentInPost.post, post)
                .where(
                        commentInPost.id.eq(id),
                        post.id.eq(postId))
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
