package com.nameless.spin_off.service.query;

import com.nameless.spin_off.config.member.MemberDetails;
import com.nameless.spin_off.dto.CommentDto.ContentCommentDto;
import com.nameless.spin_off.dto.MemberDto.MembersByContentDto;

import java.util.List;

public interface CommentInCollectionQueryService {
    List<ContentCommentDto> getCommentsByCollectionId(MemberDetails currentMember, Long collectionId);
    List<MembersByContentDto> getLikeCommentMembers(Long memberId, Long commentId);
}
