package com.nameless.spin_off.service.comment;

import com.nameless.spin_off.dto.CommentDto.CommentInPostRequestDto;
import com.nameless.spin_off.exception.comment.AlreadyLikedCommentInPostException;
import com.nameless.spin_off.exception.comment.NotExistCommentInPostException;
import com.nameless.spin_off.exception.member.NotExistMemberException;
import com.nameless.spin_off.exception.post.NotExistPostException;

public interface CommentInPostService {
    Long insertCommentInPostByCommentVO(CommentInPostRequestDto commentVO, Long memberId, Long postId)
            throws NotExistMemberException, NotExistPostException, NotExistCommentInPostException;
    Long insertLikedCommentByMemberId(Long memberId, Long commentId) throws NotExistMemberException, NotExistCommentInPostException, AlreadyLikedCommentInPostException;
}
