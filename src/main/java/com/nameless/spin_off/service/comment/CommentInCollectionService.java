package com.nameless.spin_off.service.comment;

import com.nameless.spin_off.dto.CommentDto.CreateCommentInCollectionVO;
import com.nameless.spin_off.exception.collection.NotExistCollectionException;
import com.nameless.spin_off.exception.comment.AlreadyLikedCommentInCollectionException;
import com.nameless.spin_off.exception.comment.NotExistCommentInCollectionException;
import com.nameless.spin_off.exception.member.NotExistMemberException;

public interface CommentInCollectionService {
    Long insertCommentInCollectionByCommentVO(CreateCommentInCollectionVO commentVO, Long memberId, Long collectionId)
            throws NotExistMemberException, NotExistCollectionException, NotExistCommentInCollectionException;
    Long insertLikedCommentByMemberId(Long memberId, Long commentId)
            throws NotExistMemberException, NotExistCommentInCollectionException, AlreadyLikedCommentInCollectionException;
}
