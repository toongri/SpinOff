package com.nameless.spin_off.service.comment;

import com.nameless.spin_off.dto.CommentDto.CreateCommentInPostVO;
import com.nameless.spin_off.entity.comment.CommentInPost;
import com.nameless.spin_off.exception.comment.NoSuchCommentInPostException;
import com.nameless.spin_off.exception.member.NoSuchMemberException;
import com.nameless.spin_off.exception.post.NoSuchPostException;

public interface CommentInPostService {
    CommentInPost saveCommentInPostByCommentVO(CreateCommentInPostVO commentVO) throws NoSuchMemberException, NoSuchPostException, NoSuchCommentInPostException;
}
