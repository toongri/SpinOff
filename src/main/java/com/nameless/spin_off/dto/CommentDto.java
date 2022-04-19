package com.nameless.spin_off.dto;

import com.nameless.spin_off.dto.MemberDto.CommentMemberDto;
import com.querydsl.core.annotations.QueryProjection;
import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

public class CommentDto {

    @Data
    @NoArgsConstructor
    public static class ContentCommentDto {
        private Long commentId;
        private CommentMemberDto member;
        private String content;
        private LocalDateTime createTime;
        private boolean isDeleted;
        private boolean isLiked;
        private boolean hasAuth;
        private boolean isBlocked;
        private Long likeSize;
        private Long parentId;
        private List<ContentCommentDto> children = new ArrayList<>();

        @QueryProjection
        public ContentCommentDto(Long commentId, Long memberId, String profile, String nickname,
                                 String content, LocalDateTime createTime,
                                 boolean isDeleted, Long likeSize, Long parentId) {
            this.commentId = commentId;
            this.member = new CommentMemberDto(memberId, profile, nickname);
            this.content = content;
            this.createTime = createTime;
            this.isDeleted = isDeleted;
            this.likeSize = likeSize;
            this.parentId = parentId;
        }

        public void setBoolean(Long memberId, boolean hasAuth, boolean isLiked) {
            this.hasAuth = hasAuth || member.getMemberId().equals(memberId);
            this.isLiked = isLiked;
        }

        public void setChildren(List<ContentCommentDto> children) {
            if (children != null)
                this.children = children;
        }
    }


    @Data
    @AllArgsConstructor
    @NoArgsConstructor
    public static class CreateCommentInPostVO {

        @ApiModelProperty(
                value = "댓글 id",
                example = "123",
                dataType = "Long")
        private Long parentId;

        @ApiModelProperty(
                value = "댓글 내용",
                required = true,
                example = "스프링부트와 aws로 혼자 구현하는 웹 서비스",
                dataType = "String")
        private String content;
    }

    @Data
    @AllArgsConstructor
    @NoArgsConstructor
    public static class CreateCommentInCollectionVO {

        @ApiModelProperty(
                value = "댓글 id",
                example = "123",
                dataType = "Long")
        private Long parentId;

        @ApiModelProperty(
                value = "댓글 내용",
                required = true,
                example = "스프링부트와 aws로 혼자 구현하는 웹 서비스",
                dataType = "String")
        private String content;
    }
}
