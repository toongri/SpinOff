package com.nameless.spin_off.service.comment;

import com.nameless.spin_off.dto.CollectionDto.CollectionIdAndPublicCollectionDto;
import com.nameless.spin_off.dto.CommentDto.CreateCommentInCollectionVO;
import com.nameless.spin_off.entity.collection.Collection;
import com.nameless.spin_off.entity.comment.CommentInCollection;
import com.nameless.spin_off.entity.comment.LikedCommentInCollection;
import com.nameless.spin_off.entity.enums.ErrorEnum;
import com.nameless.spin_off.entity.enums.collection.PublicOfCollectionStatus;
import com.nameless.spin_off.entity.member.Member;
import com.nameless.spin_off.exception.collection.NotExistCollectionException;
import com.nameless.spin_off.exception.comment.AlreadyLikedCommentInCollectionException;
import com.nameless.spin_off.exception.comment.NotExistCommentInCollectionException;
import com.nameless.spin_off.exception.member.NotExistMemberException;
import com.nameless.spin_off.exception.security.DontHaveAuthorityException;
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
    public Long insertCommentInCollectionByCommentVO(CreateCommentInCollectionVO commentVO, Long memberId, Long collectionId)
            throws NotExistMemberException, NotExistCollectionException, NotExistCommentInCollectionException {
        hasAuthCollection(memberId, collectionId, getPublicOfCollection(collectionId));
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

        CollectionIdAndPublicCollectionDto publicAndId = getPublicAndIdCollectionByCommentId(commentId);
        hasAuthCollection(memberId, publicAndId.getCollectionId(), publicAndId.getPublicOfCollectionStatus());
        isBlockMembersComment(memberId, commentId);
        isExistLikedCommentInCollection(memberId, commentId);

        return likedCommentInCollectionRepository.save(
                LikedCommentInCollection.createLikedCommentInCollection(
                        Member.createMember(memberId),
                        CommentInCollection.createCommentInCollection(commentId))).getId();
    }

    private void isBlockMembersComment(Long memberId, Long commentId) {
        if (commentId != null) {
            if (commentInCollectionQueryRepository.isBlockMembersComment(memberId, commentId)) {
                throw new DontHaveAuthorityException(ErrorEnum.DONT_HAVE_AUTHORITY);
            }
        }
    }

    private void isExistCommentInCollection(Long commentId, Long collectionId) {
        if (commentId != null) {
            if (!commentInCollectionQueryRepository.isExistInCollection(commentId, collectionId)) {
                throw new NotExistCommentInCollectionException(ErrorEnum.NOT_EXIST_COMMENT_IN_COLLECTION);
            }
        }
    }

    private PublicOfCollectionStatus getPublicOfCollection(Long collectionId) {
        return collectionQueryRepository.findPublicByCollectionId(collectionId)
                .orElseThrow(() -> new NotExistCollectionException(ErrorEnum.NOT_EXIST_COLLECTION));
    }

    private CollectionIdAndPublicCollectionDto getPublicAndIdCollectionByCommentId(Long commentId) {
        return collectionQueryRepository
                .findPublicAndIdByCommentId(commentId)
                .orElseThrow(() -> new NotExistCommentInCollectionException(ErrorEnum.NOT_EXIST_COMMENT_IN_COLLECTION));
    }

    private void hasAuthCollection(Long memberId, Long collectionId, PublicOfCollectionStatus publicOfCollectionStatus) {
        if (publicOfCollectionStatus.equals(PublicOfCollectionStatus.A)) {
            if (collectionQueryRepository.isBlockMembersCollection(memberId, collectionId)) {
                throw new DontHaveAuthorityException(ErrorEnum.DONT_HAVE_AUTHORITY);
            }
        } else if (publicOfCollectionStatus.equals(PublicOfCollectionStatus.C)){
            if (!collectionQueryRepository.isFollowMembersOrOwnerCollection(memberId, collectionId)) {
                throw new DontHaveAuthorityException(ErrorEnum.DONT_HAVE_AUTHORITY);
            }
        } else if (publicOfCollectionStatus.equals(PublicOfCollectionStatus.B)){
            if (!memberId.equals(collectionQueryRepository.findOwnerIdByCollectionId(collectionId).orElseGet(() -> null))) {
                throw new DontHaveAuthorityException(ErrorEnum.DONT_HAVE_AUTHORITY);
            }
        }
    }

    private CommentInCollection getParentCommentById(Long parentId) {
        return parentId == null ? null : CommentInCollection.createCommentInCollection(parentId);
    }

    private void isExistLikedCommentInCollection(Long memberId, Long commentId) {
        if (commentInCollectionQueryRepository.isExistLikedCommentInCollection(memberId, commentId)) {
            throw new AlreadyLikedCommentInCollectionException(ErrorEnum.ALREADY_LIKED_COMMENT_IN_COLLECTION);
        }
    }
}
