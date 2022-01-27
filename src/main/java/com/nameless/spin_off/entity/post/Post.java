package com.nameless.spin_off.entity.post;

import com.nameless.spin_off.dto.PostDto;
import com.nameless.spin_off.entity.listener.BaseTimeEntity;
import com.nameless.spin_off.entity.member.Member;
import com.nameless.spin_off.entity.movie.MovieInPost;
import com.sun.istack.NotNull;
import lombok.*;

import javax.persistence.*;
import java.util.ArrayList;
import java.util.List;

@Entity
@Getter
@ToString
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Post extends BaseTimeEntity {

    @Id @GeneratedValue
    @Column(name="post_id")
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "member_id")
    @NotNull
    private Member member;

    private String title;

    private String content;

    @Enumerated(EnumType.STRING)
    @Column(name = "post_public_status")
    private PostPublicStatus postPublicStatus;

    @OneToMany(mappedBy = "post", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private List<Comment> comments = new ArrayList<>();

    @OneToMany(mappedBy = "post", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private List<PostLike> postLikes = new ArrayList<>();

    @OneToMany(mappedBy = "post", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private List<PostedHashtag> postedHashtags = new ArrayList<>();

    @OneToMany(mappedBy = "post", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private List<Media> medias = new ArrayList<>();

    @OneToMany(mappedBy = "post", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private List<MovieInPost> movieInPosts = new ArrayList<>();

    //==연관관계 메소드==//

    public void addComment(Comment comment) {
        this.comments.add(comment);
        comment.updatePost(this);
    }

    public void addPostLike(PostLike postLike) {
        this.postLikes.add(postLike);
        postLike.updatePost(this);
    }

    public void addPostedHashTag(PostedHashtag postedHashTag) {
        this.postedHashtags.add(postedHashTag);
        postedHashTag.updatePost(this);
    }

    public void addMedia(Media media) {
        this.medias.add(media);
        media.updatePost(this);
    }

    public void addMovieInPost(MovieInPost movieInPost) {
        this.movieInPosts.add(movieInPost);
        movieInPost.updatePost(this);
    }

    //==생성 메소드==//
    public static Post createPost(Member member, String title, String content
            , List<PostedHashtag> postedHashtags, List<Media> medias, List<MovieInPost> movieInPosts
            , PostPublicStatus postPublicStatus) {
        Post post = new Post();
        post.updateMember(member);
        post.updateTitle(title);
        post.updateContent(content);
        post.updatePostedHashtag(postedHashtags);
        post.updateMedia(medias);
        post.updatePublicStatus(postPublicStatus);
        post.updateMovieInPost(movieInPosts);

        return post;
    }

    public static PostDto.PostBuilder buildPost() {
        return new PostDto.PostBuilder();
    }

    //==수정 메소드==//
    public void updatePublicStatus(PostPublicStatus publicStatus) {
        this.postPublicStatus = publicStatus;
    }

    public void updateMedia(List<Media> medias) {
        for (Media media : medias) {
            this.addMedia(media);
        }
    }

    public void updatePostedHashtag(List<PostedHashtag> postedHashtags) {
        for (PostedHashtag postedHashTag : postedHashtags) {
            this.addPostedHashTag(postedHashTag);
        }
    }

    public void updateMovieInPost(List<MovieInPost> movieInPosts) {
        for (MovieInPost movieInPost : movieInPosts) {
            this.addMovieInPost(movieInPost);
        }
    }

    public void updateContent(String content) {
        this.content = content;
    }

    public void updateTitle(String title) {
        this.title = title;
    }

    public void updateMember(Member member) {
        this.member = member;
    }

    //==비즈니스 로직==//

    //==조회 로직==//
}