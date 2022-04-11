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

        public void setChildren(List<ContentCommentDto> children) {
            if (children != null)
                this.children = children;
        }
        //        public ContentCommentDto(CommentInPost comment, Long memberId, List<Member> blockedMembers,
//                                 List<Member> followedMembers, boolean isAdmin) {
//            this.isDeleted = comment.getIsDeleted();
//
//            if (!this.isDeleted) {
//                this.isBlocked = blockedMembers.contains(comment.getMember());
//
//                if (!this.isBlocked) {
//                    this.commentId = comment.getId();
//                    this.member = new CommentMemberDto(comment.getMember());
//                    this.content = comment.getContent();
//                    this.createTime = comment.getCreatedDate();
//                    this.isDeleted = comment.getIsDeleted();
//                    this.hasAuth = comment.getMember().getId().equals(memberId) || isAdmin;
//
//                    this.likeMembers = getLikedMembers(comment.getLikedCommentInPosts(), blockedMembers, followedMembers);
//
//                    this.likeSize = this.likeMembers.size();
//                    this.isLiked = this.likeMembers
//                            .stream().anyMatch(contentMemberDto -> contentMemberDto.getMemberId().equals(memberId));
//
//                    this.children = comment.getChildren().stream()
//                            .map(commentInPost -> new ContentCommentDto(
//                                    commentInPost, memberId, blockedMembers, followedMembers, isAdmin))
//                            .collect(Collectors.toList());
//                }
//            }
//        }

//        private List<ContentMemberDto> getLikedMembers(List<LikedCommentInPost> likedCommentInPosts,
//                                                       List<Member> blockedMembers, List<Member> followedMembers) {
//            return likedCommentInPosts.stream()
//                    .filter(likedCommentInPost ->
//                            !blockedMembers.contains(likedCommentInPost.getMember()))
//                    .map(likedCommentInPost -> new ContentMemberDto(
//                            likedCommentInPost.getMember(), followedMembers.contains(likedCommentInPost.getMember())))
//                    .sorted(Comparator.comparing(ContentMemberDto::isFollowed, Comparator.reverseOrder()))
//                    .collect(Collectors.toList());
//        }
    }


    @Data
    @AllArgsConstructor
    @NoArgsConstructor
    public static class CreateCommentInPostVO {

        @ApiModelProperty(
                value = "글 id",
                required = true,
                example = "123",
                dataType = "Long")
        private Long postId;

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
                value = "컬렉션 id",
                required = true,
                example = "123",
                dataType = "Long")
        private Long collectionId;

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
