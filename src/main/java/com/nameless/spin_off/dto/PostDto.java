package com.nameless.spin_off.dto;

import com.nameless.spin_off.dto.HashtagDto.ContentHashtagDto;
import com.nameless.spin_off.dto.MemberDto.ContentMemberDto;
import com.nameless.spin_off.dto.MovieDto.MovieInVisitPostDto;
import com.nameless.spin_off.entity.enums.post.PublicOfPostStatus;
import com.nameless.spin_off.entity.hashtag.Hashtag;
import com.nameless.spin_off.entity.member.Member;
import com.nameless.spin_off.entity.movie.Movie;
import com.nameless.spin_off.entity.post.Post;
import com.nameless.spin_off.exception.post.AlreadyPostedHashtagException;
import com.nameless.spin_off.exception.post.IncorrectContentOfPostException;
import com.nameless.spin_off.exception.post.IncorrectTitleOfPostException;
import com.querydsl.core.annotations.QueryProjection;
import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.domain.Slice;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Objects;

public class PostDto {

    @Data
    @NoArgsConstructor
    public static class VisitPostDto {
        private Long postId;
        private ContentMemberDto member;
        private String postTitle;
        private LocalDateTime createTime;
        private LocalDateTime getLastModifiedDate;
        private String postContent;
        private MovieInVisitPostDto movie;
        private PublicOfPostStatus publicOfPostStatus;
        private boolean hasAuth;
        private List<ContentHashtagDto> hashtags;
        private Long likedSize;
        private Long commentSize;
        private boolean isLiked;

        public void setIsLiked(boolean isLiked) {
            this.isLiked = isLiked;
        }

        public void setHasAuth(Long memberId, boolean isAdmin) {
            this.hasAuth = this.member.getMemberId().equals(memberId) || isAdmin;
        }

        @QueryProjection
        public VisitPostDto(Long postId, Long memberId, String profile, String nickname, String accountId,
                            String postTitle, LocalDateTime createTime, LocalDateTime getLastModifiedDate,
                            String postContent, String movieThumbnail, String movieTitle, String directorName,
                            Long likedSize, Long commentSize, PublicOfPostStatus publicOfPostStatus) {

            this.postId = postId;
            this.member = new ContentMemberDto(memberId, profile, nickname, accountId);
            this.postTitle = postTitle;
            this.createTime = createTime;
            this.publicOfPostStatus = publicOfPostStatus;
            this.getLastModifiedDate = getLastModifiedDate;
            this.postContent = postContent;
            this.movie = new MovieInVisitPostDto(movieThumbnail, movieTitle, directorName);
            this.likedSize = likedSize;
            this.commentSize = commentSize;
        }
    }

    @Data
    @NoArgsConstructor
    public static class SearchPageAtHashtagPostDto {

        private Long postId;
        private String postTitle;
        private Long memberId;
        private String memberNickname;
        private String memberProfileImgUrl;
        private String thumbnailUrl;

        @QueryProjection
        public SearchPageAtHashtagPostDto(Long postId, String title, Long memberId, String memberNickname,
                                      String memberProfileImgUrl, String thumbnailUrl) {
            this.postId = postId;
            this.postTitle = title;
            this.memberId = memberId;
            this.memberNickname = memberNickname;
            this.memberProfileImgUrl = memberProfileImgUrl;
            this.thumbnailUrl = thumbnailUrl;
        }
    }

    @Data
    public static class IdAndPublicPostDto {
        private Long postId;
        private PublicOfPostStatus publicOfPostStatus;

        @QueryProjection
        public IdAndPublicPostDto(Long postId, PublicOfPostStatus publicOfPostStatus) {
            this.postId = postId;
            this.publicOfPostStatus = publicOfPostStatus;
        }
    }

    @Data
    @NoArgsConstructor
    public static class SearchPageAtAllPostDto {

        private Long postId;
        private String postTitle;
        private Long memberId;
        private String memberNickname;
        private String memberProfileImgUrl;
        private String thumbnailUrl;

        @QueryProjection
        public SearchPageAtAllPostDto(Long postId, String title, Long memberId, String memberNickname,
                               String memberProfileImgUrl, String thumbnailUrl) {
            this.postId = postId;
            this.postTitle = title;
            this.memberId = memberId;
            this.memberNickname = memberNickname;
            this.memberProfileImgUrl = memberProfileImgUrl;
            this.thumbnailUrl = thumbnailUrl;
        }
    }

    @Data
    @NoArgsConstructor
    public static class RelatedSearchPostDto {

        private Long id;
        private String title;

        @QueryProjection
        public RelatedSearchPostDto(Long id, String title) {
            this.id = id;
            this.title = title;
        }
    }

    @Data
    @NoArgsConstructor
    public static class MainPagePostDto {

        private Long postId;
        private String postTitle;
        private Long memberId;
        private String memberNickname;
        private String memberProfileImgUrl;
        private String thumbnailUrl;

        @QueryProjection
        public MainPagePostDto(Long postId, String title, Long memberId, String memberNickname,
                               String memberProfileImgUrl, String thumbnailUrl) {
            this.postId = postId;
            this.postTitle = title;
            this.memberId = memberId;
            this.memberNickname = memberNickname;
            this.memberProfileImgUrl = memberProfileImgUrl;
            this.thumbnailUrl = thumbnailUrl;
        }

        @Override
        public int hashCode() {
            return Objects.hash(postId);
        }

        @Override
        public boolean equals(Object mainPagePostDto) {
            if (mainPagePostDto instanceof MainPagePostDto) {
                return ((MainPagePostDto) mainPagePostDto).postId.equals(postId);
            }
            return false;
        }
    }

    @Data
    @AllArgsConstructor
    public static class RelatedPostFirstDto<T> {
        private T data;
        private Slice<RelatedPostDto> posts;
    }

    @Data
    @NoArgsConstructor
    public static class RelatedPostDto {

        private Long postId;
        private String postTitle;
        private Long memberId;
        private String memberNickname;
        private String memberProfileImgUrl;
        private String thumbnailUrl;

        @QueryProjection
        public RelatedPostDto(Long postId, String title, Long memberId, String memberNickname,
                               String memberProfileImgUrl, String thumbnailUrl) {
            this.postId = postId;
            this.postTitle = title;
            this.memberId = memberId;
            this.memberNickname = memberNickname;
            this.memberProfileImgUrl = memberProfileImgUrl;
            this.thumbnailUrl = thumbnailUrl;
        }

        @Override
        public int hashCode() {
            return Objects.hash(postId);
        }

        @Override
        public boolean equals(Object mainPagePostDto) {
            if (mainPagePostDto instanceof MainPagePostDto) {
                return ((MainPagePostDto) mainPagePostDto).postId.equals(postId);
            }
            return false;
        }
    }

    @Data
    @AllArgsConstructor
    @NoArgsConstructor
    public static class CreatePostVO {
        @ApiModelProperty(
                value = "글 제목",
                required = true,
                example = "스프링부트와 aws로 혼자 구현하는 웹 서비스",
                dataType = "String")
        private String title;

        @ApiModelProperty(
                value = "글 제목",
                required = true,
                example = "스프링부트와 aws로 혼자 구현하는 웹 서비스",
                dataType = "String")
        private String content;

        @ApiModelProperty(
                value = "글 제목",
                required = true,
                example = "스프링부트와 aws로 혼자 구현하는 웹 서비스",
                dataType = "String")
        private Long movieId;

        @ApiModelProperty(
                value = "글 제목",
                required = true,
                example = "스프링부트와 aws로 혼자 구현하는 웹 서비스",
                dataType = "String")
        private PublicOfPostStatus publicOfPostStatus;

        @ApiModelProperty(
                value = "글 제목",
                required = true,
                example = "스프링부트와 aws로 혼자 구현하는 웹 서비스",
                dataType = "String")
        private List<String> hashtagContents;

        @ApiModelProperty(
                value = "글 제목",
                required = true,
                example = "스프링부트와 aws로 혼자 구현하는 웹 서비스",
                dataType = "List<Long>")
        private List<Long> collectionIds;
    }

    public static class PostBuilder {

        private Member member;
        private String title;
        private String content;
        private Movie movie;
        private String thumbnailUrl;
        private PublicOfPostStatus publicOfPostStatus = PublicOfPostStatus.B;
        private List<String> urls;
        private List<Hashtag> hashtags;

        public PostBuilder setMember(Member member) {
            this.member = member;
            return this;
        }

        public PostBuilder setTitle(String title) {
            this.title = title;
            return this;
        }

        public PostBuilder setContent(String content) {
            this.content = content;
            return this;
        }

        public PostBuilder setPostPublicStatus(PublicOfPostStatus publicOfPostStatus) {
            this.publicOfPostStatus = publicOfPostStatus;
            return this;
        }

        public PostBuilder setHashTags(List<Hashtag> hashtags) {
            this.hashtags = hashtags;
            return this;
        }

        public PostBuilder setUrls(List<String> urls) {
            this.urls = urls;
            return this;
        }

        public PostBuilder setMovie(Movie movie) {
            this.movie = movie;
            return this;
        }

        public PostBuilder setThumbnailUrl(String thumbnailUrl) {
            this.thumbnailUrl = thumbnailUrl;
            return this;
        }

        public Post build() throws AlreadyPostedHashtagException, IncorrectTitleOfPostException, IncorrectContentOfPostException {
            return Post.createPost(member, title, content, thumbnailUrl, hashtags, urls, movie, publicOfPostStatus);
        }
    }
}
