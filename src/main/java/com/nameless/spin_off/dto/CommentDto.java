package com.nameless.spin_off.dto;

import com.nameless.spin_off.dto.MemberDto.CommentMemberDto;
import com.nameless.spin_off.dto.MemberDto.ContentMemberDto;
import com.nameless.spin_off.entity.comment.CommentInPost;
import com.nameless.spin_off.entity.comment.LikedCommentInPost;
import com.nameless.spin_off.entity.member.Member;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.Comparator;
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
        private boolean isLiked;
        private boolean hasAuth;
        private List<ContentMemberDto> likeMembers;
        private int likeSize;
        private List<ContentCommentDto> children;

        public ContentCommentDto(CommentInPost comment, Long memberId, List<Member> blockedMembers,
                                 List<Member> followedMembers, boolean isAdmin) {
            this.isDeleted = comment.getIsDeleted();

            if (!this.isDeleted) {
                this.isBlocked = blockedMembers.contains(comment.getMember());

                if (!this.isBlocked) {
                    this.commentId = comment.getId();
                    this.member = new CommentMemberDto(comment.getMember());
                    this.content = comment.getContent();
                    this.createTime = comment.getCreatedDate();
                    this.isDeleted = comment.getIsDeleted();
                    this.hasAuth = comment.getMember().getId().equals(memberId) || isAdmin;

                    this.likeMembers = getLikedMembers(comment.getLikedCommentInPosts(), blockedMembers, followedMembers);

                    this.likeSize = this.likeMembers.size();
                    this.isLiked = this.likeMembers
                            .stream().anyMatch(contentMemberDto -> contentMemberDto.getMemberId().equals(memberId));

                    this.children = comment.getChildren().stream()
                            .map(commentInPost -> new ContentCommentDto(
                                    commentInPost, memberId, blockedMembers, followedMembers, isAdmin))
                            .collect(Collectors.toList());
                }
            }
        }

        private List<ContentMemberDto> getLikedMembers(List<LikedCommentInPost> likedCommentInPosts,
                                                       List<Member> blockedMembers, List<Member> followedMembers) {
            return likedCommentInPosts.stream()
                    .filter(likedCommentInPost ->
                            !blockedMembers.contains(likedCommentInPost.getMember()))
                    .map(likedCommentInPost -> new ContentMemberDto(
                            likedCommentInPost.getMember(), followedMembers.contains(likedCommentInPost.getMember())))
                    .sorted(Comparator.comparing(ContentMemberDto::isFollowed, Comparator.reverseOrder()))
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
