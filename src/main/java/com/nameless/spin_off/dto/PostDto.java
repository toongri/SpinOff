package com.nameless.spin_off.dto;

import com.nameless.spin_off.entity.collections.CollectedPost;
import com.nameless.spin_off.entity.member.Member;
import com.nameless.spin_off.entity.movie.Movie;
import com.nameless.spin_off.entity.movie.PostedMovie;
import com.nameless.spin_off.entity.post.*;
import com.querydsl.core.annotations.QueryProjection;
import lombok.*;

import java.util.ArrayList;
import java.util.List;

public class PostDto {

    @Data
    @NoArgsConstructor
    public static class MainPagePostDto {
        private String imgUrl;
        private String title;
        private String memberProfileImg;

        @QueryProjection
        public MainPagePostDto(String imgUrl, String title, String memberProfileImg) {
            this.imgUrl = imgUrl;
            this.title = title;
            this.memberProfileImg = memberProfileImg;
        }
    }

    @Data
    @AllArgsConstructor
    @NoArgsConstructor
    public static class CreatePostVO {

        private Long memberId;
        private String title;
        private String content;
        private PublicOfPostStatus publicOfPostStatus;
        private List<String> hashtagContents;
        private List<String> mediaUrls;
        private List<Long> movieIds;
        private List<Long> collectionIds;
    }

    public static class PostBuilder {

        private Member member;
        private String title;
        private String content;
        private PublicOfPostStatus publicOfPostStatus = PublicOfPostStatus.PRIVATE;
        private List<PostedMedia> postedMedias = new ArrayList<>();
        private List<Hashtag> hashtags = new ArrayList<>();
        private List<Movie> movies = new ArrayList<>();

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
            this.hashtags.addAll(hashtags);
            return this;
        }

        public PostBuilder setPostedMedias(List<PostedMedia> postedMedia) {
            this.postedMedias.addAll(postedMedia);
            return this;
        }

        public PostBuilder setMovies(List<Movie> movies) {
            this.movies.addAll(movies);
            return this;
        }

        public Post build() {
            return Post.createPost(member, title, content, hashtags, postedMedias, movies, publicOfPostStatus);
        }
    }
}
