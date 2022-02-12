package com.nameless.spin_off.service.comment;

import com.nameless.spin_off.dto.CommentDto.CreateCommentInPostVO;
import com.nameless.spin_off.entity.comment.CommentInPost;
import com.nameless.spin_off.exception.comment.NotExistCommentInPostException;
import com.nameless.spin_off.exception.member.NotExistMemberException;
import com.nameless.spin_off.exception.post.NotExistPostException;

public interface CommentInPostService {
    CommentInPost insertCommentInPostByCommentVO(CreateCommentInPostVO commentVO)
            throws NotExistMemberException, NotExistPostException, NotExistCommentInPostException;
}
