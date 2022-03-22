package com.nameless.spin_off.dto;

import com.nameless.spin_off.dto.MemberDto.CommentMemberDto;
import com.nameless.spin_off.dto.MemberDto.ContentMemberDto;
import com.nameless.spin_off.entity.comment.CommentInPost;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

public class CommentDto {

    @Data
    @NoArgsConstructor
    public static class ContentCommentDto {
        private Long commentId;
        private CommentMemberDto member;
        private String content;
        private LocalDateTime createTime;
        private boolean isDeleted;
        private boolean isBlocked;
        private boolean hasAuth;
        private int likeSize;
        private List<ContentMemberDto> likeMembers;

        public ContentCommentDto(CommentInPost comment, Long memberId) {
            this.commentId = comment.getId();
            this.member = new CommentMemberDto(comment.getMember());
            this.content = comment.getContent();
            this.createTime = comment.getCreatedDate();
            this.isDeleted = comment.getIsDeleted();
            this.hasAuth = comment.getMember().getId().equals(memberId);
            this.likeSize = comment.getLikedCommentInPosts().size();
            this.likeMembers = comment.getLikedCommentInPosts().stream()
                    .map(likedCommentInPost -> new ContentMemberDto(likedCommentInPost.getMember()))
                    .collect(Collectors.toList());
        }
    }


    @Data
    @AllArgsConstructor
    @NoArgsConstructor
    public static class CreateCommentInPostVO {
        private Long postId;
        private Long parentId;
        private String content;
    }

    @Data
    @AllArgsConstructor
    @NoArgsConstructor
    public static class CreateCommentInCollectionVO {
        private Long collectionId;
        private Long parentId;
        private String content;
    }
}
