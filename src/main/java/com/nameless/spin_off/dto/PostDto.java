package com.nameless.spin_off.dto;

import com.nameless.spin_off.entity.collection.Collection;
import com.nameless.spin_off.entity.enums.post.PublicOfPostStatus;
import com.nameless.spin_off.entity.hashtag.Hashtag;
import com.nameless.spin_off.entity.member.Member;
import com.nameless.spin_off.entity.movie.Movie;
import com.nameless.spin_off.entity.post.Post;
import com.nameless.spin_off.entity.post.PostedMedia;
import com.nameless.spin_off.exception.post.AlreadyAuthorityOfPostStatusException;
import com.nameless.spin_off.exception.post.AlreadyPostedHashtagException;
import com.nameless.spin_off.exception.post.OverContentOfPostException;
import com.nameless.spin_off.exception.post.OverTitleOfPostException;
import com.querydsl.core.annotations.QueryProjection;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;
import java.util.Objects;

public class PostDto {

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
    @NoArgsConstructor
    public static class CreatePostVO {
        private String title;
        private String content;
        private Long movieId;
        private String thumbnailUrl;
        private PublicOfPostStatus publicOfPostStatus;
        private List<String> hashtagContents;
        private List<String> mediaUrls;
        private List<Long> collectionIds;
    }

    public static class PostBuilder {

        private Member member;
        private String title;
        private String content;
        private Movie movie;
        private String thumbnailUrl;
        private PublicOfPostStatus publicOfPostStatus = PublicOfPostStatus.B;
        private List<PostedMedia> postedMedias;
        private List<Hashtag> hashtags;
        private List<Collection> collections;

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

        public PostBuilder setPostedMedias(List<PostedMedia> postedMedia) {
            this.postedMedias = postedMedia;
            return this;
        }

        public PostBuilder setCollections(List<Collection> collections) {
            this.collections = collections;
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

        public Post build() throws AlreadyPostedHashtagException, AlreadyAuthorityOfPostStatusException, OverTitleOfPostException, OverContentOfPostException {
            return Post.createPost(member, title, content, thumbnailUrl, hashtags, postedMedias, collections, movie, publicOfPostStatus);
        }
    }
}
