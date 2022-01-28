package com.nameless.spin_off.dto;

import com.nameless.spin_off.entity.collections.CollectedPost;
import com.nameless.spin_off.entity.member.Member;
import com.nameless.spin_off.entity.movie.PostedMovie;
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
        private List<PostedHashtag> postedHashtags = new ArrayList<>();
        private List<PostedMedia> postedMedias = new ArrayList<>();
        private List<PostedMovie> postedMovies = new ArrayList<>();

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

        public PostBuilder setPostedHashTags(List<PostedHashtag> postedHashtags) {
            this.postedHashtags.addAll(postedHashtags);
            return this;
        }

        public PostBuilder setPostedMedias(List<PostedMedia> postedMedia) {
            this.postedMedias.addAll(postedMedia);
            return this;
        }

        public PostBuilder setPostedMovies(List<PostedMovie> postedMovies) {
            this.postedMovies.addAll(postedMovies);
            return this;
        }

        public Post build() {
            return Post.createPost(member, title, content, postedHashtags, postedMedias, postedMovies, publicOfPostStatus);
        }
    }
}
