package com.nameless.spin_off.service.comment;

import com.nameless.spin_off.dto.CollectionDto;
import com.nameless.spin_off.dto.CollectionDto.CollectionIdAndOwnerIdAndPublicCollectionDto;
import com.nameless.spin_off.dto.CollectionDto.CollectionIdAndPublicCollectionDto;
import com.nameless.spin_off.dto.CollectionDto.OwnerIdAndPublicCollectionDto;
import com.nameless.spin_off.dto.CommentDto.CommentInCollectionRequestDto;
import com.nameless.spin_off.entity.collection.Collection;
import com.nameless.spin_off.entity.comment.CommentInCollection;
import com.nameless.spin_off.entity.comment.LikedCommentInCollection;
import com.nameless.spin_off.enums.ErrorEnum;
import com.nameless.spin_off.enums.collection.PublicOfCollectionStatus;
import com.nameless.spin_off.entity.member.Member;
import com.nameless.spin_off.enums.member.BlockedMemberStatus;
import com.nameless.spin_off.exception.collection.NotExistCollectionException;
import com.nameless.spin_off.exception.comment.AlreadyLikedCommentInCollectionException;
import com.nameless.spin_off.exception.comment.NotExistCommentInCollectionException;
import com.nameless.spin_off.exception.member.NotExistMemberException;
import com.nameless.spin_off.exception.security.DontHaveAuthorityException;
import com.nameless.spin_off.repository.comment.CommentInCollectionRepository;
import com.nameless.spin_off.repository.comment.LikedCommentInCollectionRepository;
import com.nameless.spin_off.repository.query.CollectionQueryRepository;
import com.nameless.spin_off.repository.query.CommentInCollectionQueryRepository;
import com.nameless.spin_off.repository.query.MemberQueryRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class CommentInCollectionServiceJpa implements CommentInCollectionService {
    private final CommentInCollectionRepository commentInCollectionRepository;
    private final MemberQueryRepository memberQueryRepository;
    private final CollectionQueryRepository collectionQueryRepository;
    private final CommentInCollectionQueryRepository commentInCollectionQueryRepository;
    private final LikedCommentInCollectionRepository likedCommentInCollectionRepository;

    @Transactional
    @Override
    public Long insertCommentInCollectionByCommentVO(CommentInCollectionRequestDto commentVO, Long memberId, Long collectionId)
            throws NotExistMemberException, NotExistCollectionException, NotExistCommentInCollectionException {

        OwnerIdAndPublicCollectionDto collectionInfo = getCollectionInfo(collectionId);
        hasAuthCollection(memberId, collectionInfo.getPublicOfCollectionStatus(), collectionInfo.getCollectionOwnerId());
        isExistCommentInCollection(commentVO.getParentId(), collectionId);
        isBlockMembersComment(memberId, commentVO.getParentId());

        return commentInCollectionRepository.save(CommentInCollection
                .createCommentInCollection(
                        Member.createMember(memberId),
                        commentVO.getContent(),
                        getParentCommentById(commentVO.getParentId()),
                        Collection.createCollection(collectionId))).getId();
    }

    private OwnerIdAndPublicCollectionDto getCollectionInfo(Long collectionId) {
        return collectionQueryRepository.findCollectionOwnerIdAndPublic(collectionId).orElseThrow(() -> new NotExistCollectionException(ErrorEnum.NOT_EXIST_COLLECTION));
    }

    @Transactional
    @Override
    public Long insertLikedCommentByMemberId(Long memberId, Long commentId)
            throws NotExistMemberException, NotExistCommentInCollectionException, AlreadyLikedCommentInCollectionException {

        CollectionIdAndOwnerIdAndPublicCollectionDto collectionInfo = getPublicAndIdCollectionByCommentId(commentId);
        hasAuthCollection(memberId, collectionInfo.getPublicOfCollectionStatus(), collectionInfo.getCollectionOwnerId());
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

    private CollectionIdAndOwnerIdAndPublicCollectionDto getPublicAndIdCollectionByCommentId(Long commentId) {
        return collectionQueryRepository
                .findPublicAndOwnerIdByCommentId(commentId)
                .orElseThrow(() -> new NotExistCommentInCollectionException(ErrorEnum.NOT_EXIST_COMMENT_IN_COLLECTION));
    }

    private void hasAuthCollection(Long memberId, PublicOfCollectionStatus publicOfCollectionStatus, Long collectionOwnerId) {
        if (publicOfCollectionStatus.equals(PublicOfCollectionStatus.A)) {
            if (memberQueryRepository.isBlockedOrBlockingAndStatus(memberId, collectionOwnerId, BlockedMemberStatus.A)) {
                throw new DontHaveAuthorityException(ErrorEnum.DONT_HAVE_AUTHORITY);
            }
        } else if (publicOfCollectionStatus.equals(PublicOfCollectionStatus.C)){
            if (!(memberId.equals(collectionOwnerId) || memberQueryRepository.isExistFollowedMember(memberId, collectionOwnerId))) {
                throw new DontHaveAuthorityException(ErrorEnum.DONT_HAVE_AUTHORITY);
            }
        } else if (publicOfCollectionStatus.equals(PublicOfCollectionStatus.B)){
            if (!memberId.equals(collectionOwnerId)) {
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
