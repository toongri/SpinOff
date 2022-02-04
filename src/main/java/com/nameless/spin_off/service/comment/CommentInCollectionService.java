package com.nameless.spin_off.service.comment;

import com.nameless.spin_off.dto.CommentDto;
import com.nameless.spin_off.dto.CommentDto.CreateCommentInCollectionVO;
import com.nameless.spin_off.entity.comment.CommentInCollection;
import com.nameless.spin_off.exception.collection.NoSuchCollectionException;
import com.nameless.spin_off.exception.comment.NoSuchCommentInCollectionException;
import com.nameless.spin_off.exception.member.NoSuchMemberException;

public interface CommentInCollectionService {
    CommentInCollection saveCommentInCollectionByCommentVO(CreateCommentInCollectionVO commentVO) throws NoSuchMemberException, NoSuchCollectionException, NoSuchCommentInCollectionException;
}
