package com.nameless.spin_off.dto;

import com.nameless.spin_off.entity.member.Member;
import com.nameless.spin_off.entity.movie.Movie;
import com.nameless.spin_off.entity.post.*;
import com.nameless.spin_off.exception.post.AlreadyPostedHashtagException;
import lombok.*;

import java.util.ArrayList;
import java.util.List;

public class PostDto {

    @Data
    @NoArgsConstructor
    public static class MainPagePostDto {

        private Long postId;
        private String postTitle;
        private Long memberId;
        private String memberNickname;
        private String memberProfileImgUrl;
        private String thumbnailUrl;

        public MainPagePostDto(Post post) {
            this.postId = post.getId();
            this.postTitle = post.getTitle();
            this.memberId = post.getMember().getId();
            this.memberNickname = post.getMember().getNickname();
            this.memberProfileImgUrl = post.getMember().getProfileImg();
            this.thumbnailUrl = post.getThumbnailUrl();
        }
    }

    @Data
    @AllArgsConstructor
    @NoArgsConstructor
    public static class CreatePostVO {

        private Long memberId;
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
        private PublicOfPostStatus publicOfPostStatus = PublicOfPostStatus.PRIVATE;
        private List<PostedMedia> postedMedias = new ArrayList<>();
        private List<Hashtag> hashtags = new ArrayList<>();

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

        public PostBuilder setMovie(Movie movie) {
            this.movie = movie;
            return this;
        }

        public PostBuilder setThumbnailUrl(String thumbnailUrl) {
            this.thumbnailUrl = thumbnailUrl;
            return this;
        }

        public Post build() throws AlreadyPostedHashtagException {
            return Post.createPost(member, title, content, thumbnailUrl, hashtags, postedMedias, movie, publicOfPostStatus);
        }
    }
}
