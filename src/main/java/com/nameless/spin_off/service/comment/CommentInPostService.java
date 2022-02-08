package com.nameless.spin_off.service.comment;

import com.nameless.spin_off.dto.CommentDto.CreateCommentInPostVO;
import com.nameless.spin_off.entity.comment.CommentInPost;
import com.nameless.spin_off.exception.comment.NotSearchCommentInPostException;
import com.nameless.spin_off.exception.member.NotSearchMemberException;
import com.nameless.spin_off.exception.post.NotSearchPostException;

public interface CommentInPostService {
    CommentInPost insertCommentInPostByCommentVO(CreateCommentInPostVO commentVO)
            throws NotSearchMemberException, NotSearchPostException, NotSearchCommentInPostException;
}
