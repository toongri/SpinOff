package com.nameless.spin_off.service.comment;

import com.nameless.spin_off.dto.CollectionDto.IdAndPublicCollectionDto;
import com.nameless.spin_off.dto.CommentDto.CreateCommentInCollectionVO;
import com.nameless.spin_off.entity.collection.Collection;
import com.nameless.spin_off.entity.comment.CommentInCollection;
import com.nameless.spin_off.entity.comment.LikedCommentInCollection;
import com.nameless.spin_off.entity.enums.collection.PublicOfCollectionStatus;
import com.nameless.spin_off.entity.member.Member;
import com.nameless.spin_off.exception.collection.NotExistCollectionException;
import com.nameless.spin_off.exception.comment.AlreadyLikedCommentInCollectionException;
import com.nameless.spin_off.exception.comment.NotExistCommentInCollectionException;
import com.nameless.spin_off.exception.member.DontHaveAccessException;
import com.nameless.spin_off.exception.member.NotExistMemberException;
import com.nameless.spin_off.repository.comment.CommentInCollectionRepository;
import com.nameless.spin_off.repository.comment.LikedCommentInCollectionRepository;
import com.nameless.spin_off.repository.query.CollectionQueryRepository;
import com.nameless.spin_off.repository.query.CommentInCollectionQueryRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class CommentInCollectionServiceJpa implements CommentInCollectionService {
    private final CommentInCollectionRepository commentInCollectionRepository;
    private final CollectionQueryRepository collectionQueryRepository;
    private final CommentInCollectionQueryRepository commentInCollectionQueryRepository;
    private final LikedCommentInCollectionRepository likedCommentInCollectionRepository;

    @Transactional
    @Override
    public Long insertCommentInCollectionByCommentVO(CreateCommentInCollectionVO commentVO, Long memberId)
            throws NotExistMemberException, NotExistCollectionException, NotExistCommentInCollectionException {
        Long collectionId = commentVO.getCollectionId();
        hasAuthPost(memberId, collectionId, getPublicOfCollection(collectionId));
        isExistCommentInCollection(commentVO.getParentId(), collectionId);
        isBlockMembersComment(memberId, commentVO.getParentId());

        return commentInCollectionRepository.save(CommentInCollection
                .createCommentInCollection(
                        Member.createMember(memberId),
                        commentVO.getContent(),
                        getParentCommentById(commentVO.getParentId()),
                        Collection.createCollection(collectionId))).getId();
    }

    @Transactional
    @Override
    public Long insertLikedCommentByMemberId(Long memberId, Long commentId)
            throws NotExistMemberException, NotExistCommentInCollectionException, AlreadyLikedCommentInCollectionException {

        IdAndPublicCollectionDto publicAndId = getPublicAndIdCollectionByCommentId(commentId);
        hasAuthPost(memberId, publicAndId.getId(), publicAndId.getPublicOfCollectionStatus());
        isBlockMembersComment(memberId, commentId);
        isExistLikedCommentInCollection(memberId, commentId);

        return likedCommentInCollectionRepository.save(
                LikedCommentInCollection.createLikedCommentInCollection(
                        Member.createMember(memberId),
                        CommentInCollection.createCommentInCollection(commentId))).getId();
    }

    private void isBlockMembersComment(Long memberId, Long commentId) {
        if (commentId == null) {

        } else if (commentInCollectionQueryRepository.isBlockMembersComment(memberId, commentId)) {
            throw new DontHaveAccessException();
        }
    }

    private void isExistCommentInCollection(Long commentId, Long collectionId) {
        if (commentId == null) {

        } else if (!commentInCollectionQueryRepository.isExistInCollection(commentId, collectionId)) {
            throw new NotExistCommentInCollectionException();
        }
    }

    private PublicOfCollectionStatus getPublicOfCollection(Long collectionId) {
        PublicOfCollectionStatus publicCollection = collectionQueryRepository.findPublicByCollectionId(collectionId);
        if (publicCollection == null) {
            throw new NotExistCollectionException();
        } else {
            return publicCollection;
        }
    }

    private IdAndPublicCollectionDto getPublicAndIdCollectionByCommentId(Long commentId) {
        return collectionQueryRepository
                .findPublicAndIdByCommentId(commentId).orElseThrow(NotExistCommentInCollectionException::new);
    }

    private void hasAuthPost(Long memberId, Long collectionId, PublicOfCollectionStatus publicOfCollectionStatus) {
        if (publicOfCollectionStatus.equals(PublicOfCollectionStatus.A)) {
            if (collectionQueryRepository.isBlockMembersCollection(memberId, collectionId)) {
                throw new DontHaveAccessException();
            }
        } else if (publicOfCollectionStatus.equals(PublicOfCollectionStatus.C)){
            if (!collectionQueryRepository.isFollowMembersCollection(memberId, collectionId)) {
                throw new DontHaveAccessException();
            }
        } else if (publicOfCollectionStatus.equals(PublicOfCollectionStatus.B)){
            if (!memberId.equals(collectionQueryRepository.findOwnerIdByCollectionId(collectionId))) {
                throw new DontHaveAccessException();
            }
        }
    }

    private CommentInCollection getParentCommentById(Long parentId) {
        return parentId == null ? null : CommentInCollection.createCommentInCollection(parentId);
    }

    private void isExistLikedCommentInCollection(Long memberId, Long commentId) {
        if (commentInCollectionQueryRepository.isExistLikedCommentInCollection(memberId, commentId)) {
            throw new AlreadyLikedCommentInCollectionException();
        }
    }
}
