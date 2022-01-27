package com.nameless.spin_off.dto;

import com.nameless.spin_off.entity.member.Member;
import com.nameless.spin_off.entity.movie.MovieInPost;
import com.nameless.spin_off.entity.post.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.ArrayList;
import java.util.List;

public class PostDto {

    @Getter
    @Setter
    @AllArgsConstructor
    @NoArgsConstructor
    public static class CreatePostVO {

        private Long memberId;
        private String title;
        private String content;
        private PostPublicStatus postPublicStatus;
        private List<String> hashtagContents;
        private List<String> mediaUrls;
        private List<Long> movieIds;
    }

    public static class PostBuilder {

        private Member member;
        private String title;
        private String content;
        private PostPublicStatus postPublicStatus = PostPublicStatus.PRIVATED;
        private List<PostedHashtag> postedHashtags = new ArrayList<>();
        private List<Media> medias = new ArrayList<>();
        private List<MovieInPost> movieInPosts = new ArrayList<>();

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

        public PostBuilder setPostPublicStatus(PostPublicStatus postPublicStatus) {
            this.postPublicStatus = postPublicStatus;
            return this;
        }

        public PostBuilder setPostedHashTags(List<PostedHashtag> postedHashtags) {
            this.postedHashtags.addAll(postedHashtags);
            return this;
        }

        public PostBuilder setMedias(List<Media> medias) {
            this.medias.addAll(medias);
            return this;
        }

        public PostBuilder setMovieInPosts(List<MovieInPost> movieInPosts) {
            this.movieInPosts.addAll(movieInPosts);
            return this;
        }

        public Post build() {
            return Post.createPost(member, title, content, postedHashtags, medias, movieInPosts, postPublicStatus);
        }
    }
}
