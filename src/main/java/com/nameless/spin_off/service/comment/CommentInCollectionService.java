package com.nameless.spin_off.service.comment;

import com.nameless.spin_off.dto.CommentDto.CreateCommentInCollectionVO;
import com.nameless.spin_off.entity.comment.CommentInCollection;
import com.nameless.spin_off.exception.collection.NotSearchCollectionException;
import com.nameless.spin_off.exception.comment.NotSearchCommentInCollectionException;
import com.nameless.spin_off.exception.member.NotSearchMemberException;

public interface CommentInCollectionService {
    CommentInCollection saveCommentInCollectionByCommentVO(CreateCommentInCollectionVO commentVO)
            throws NotSearchMemberException, NotSearchCollectionException, NotSearchCommentInCollectionException;
}
