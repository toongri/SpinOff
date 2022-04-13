package com.nameless.spin_off.service.query;

import com.nameless.spin_off.config.member.MemberDetails;
import com.nameless.spin_off.dto.CommentDto.ContentCommentDto;

import java.util.List;

public interface CommentInPostQueryService {
    List<ContentCommentDto> getCommentsByPostId(MemberDetails currentMember, Long postId);
}
