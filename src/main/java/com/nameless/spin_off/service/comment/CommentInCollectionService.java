package com.nameless.spin_off.service.comment;

import com.nameless.spin_off.dto.CommentDto.CreateCommentInCollectionVO;
import com.nameless.spin_off.entity.comment.CommentInCollection;
import com.nameless.spin_off.exception.collection.NotExistCollectionException;
import com.nameless.spin_off.exception.comment.NotExistCommentInCollectionException;
import com.nameless.spin_off.exception.member.NotExistMemberException;

public interface CommentInCollectionService {
    CommentInCollection insertCommentInCollectionByCommentVO(CreateCommentInCollectionVO commentVO)
            throws NotExistMemberException, NotExistCollectionException, NotExistCommentInCollectionException;
}
