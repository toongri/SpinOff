package com.nameless.spin_off.service.comment;

import com.nameless.spin_off.dto.CommentDto.CreateCommentInCollectionVO;
import com.nameless.spin_off.entity.collection.Collection;
import com.nameless.spin_off.entity.comment.CommentInCollection;
import com.nameless.spin_off.entity.comment.LikedCommentInCollection;
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
        isExistCollection(collectionId);
        isBlockedMemberCollection(memberId, collectionId);
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

        isExistComment(commentId);
        isBlockMembersComment(memberId, commentId);
        isExistLikedCommentInCollection(memberId, commentId);

        return likedCommentInCollectionRepository.save(
                LikedCommentInCollection.createLikedCommentInCollection(
                        Member.createMember(memberId),
                        CommentInCollection.createCommentInCollection(commentId))).getId();
    }

    private void isExistCollection(Long collectionId) {
        if (!collectionQueryRepository.isExist(collectionId)) {
            throw new NotExistCollectionException();
        }
    }

    private void isBlockedMemberCollection(Long memberId, Long collectionId) {
        if (collectionQueryRepository.isBlockMembersCollection(memberId, collectionId)) {
            throw new DontHaveAccessException();
        }
    }

    private void isBlockMembersComment(Long memberId, Long commentId) {
        if (commentId == null) {

        } else if (commentInCollectionQueryRepository.isBlockMembersComment(memberId, commentId)) {
            throw new DontHaveAccessException();
        }
    }

    private void isExistComment(Long commentId) {
        if (!commentInCollectionQueryRepository.isExist(commentId)) {
            throw new NotExistCommentInCollectionException();
        }
    }

    private void isExistCommentInCollection(Long commentId, Long collectionId) {
        if (commentId == null) {

        } else if (!commentInCollectionQueryRepository.isExistInCollection(commentId, collectionId)) {
            throw new NotExistCommentInCollectionException();
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
