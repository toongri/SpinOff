package com.nameless.spin_off.entity.post;

import com.nameless.spin_off.dto.PostDto;
import com.nameless.spin_off.entity.collections.CollectedPost;
import com.nameless.spin_off.entity.listener.BaseTimeEntity;
import com.nameless.spin_off.entity.member.Member;
import com.nameless.spin_off.entity.movie.PostedMovie;
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

    private Long view;

    @Enumerated(EnumType.STRING)
    @Column(name = "public_of_post_status")
    private PublicOfPostStatus publicOfPostStatus;

    @OneToMany(mappedBy = "post", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private List<Comment> comments = new ArrayList<>();

    @OneToMany(mappedBy = "post", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private List<LikedPost> likedPosts = new ArrayList<>();

    @OneToMany(mappedBy = "post", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private List<PostedHashtag> postedHashtags = new ArrayList<>();

    @OneToMany(mappedBy = "post", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private List<PostedMedia> postedMedias = new ArrayList<>();

    @OneToMany(mappedBy = "post", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private List<PostedMovie> postedMovies = new ArrayList<>();

    @OneToMany(mappedBy = "post", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private List<ViewedPostByIp> viewedPostByIps = new ArrayList<>();

    @OneToMany(mappedBy = "post", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private List<VisitedPostByMember> visitedPostByMembers = new ArrayList<>();

    //==연관관계 메소드==//

    public void addViewedPostByIp(ViewedPostByIp viewedPostByIp) {
        this.viewedPostByIps.add(viewedPostByIp);
        viewedPostByIp.updatePost(this);
    }

    public void addVisitedPostByMember(VisitedPostByMember visitedPostByMember) {
        this.visitedPostByMembers.add(visitedPostByMember);
        visitedPostByMember.updatePost(this);
    }

    public void addComment(Comment comment) {
        this.comments.add(comment);
        comment.updatePost(this);
    }

    public void addPostLike(LikedPost likedPost) {
        this.likedPosts.add(likedPost);
        likedPost.updatePost(this);
    }

    public void addPostedHashTag(PostedHashtag postedHashTag) {
        this.postedHashtags.add(postedHashTag);
        postedHashTag.updatePost(this);
    }

    public void addMedia(PostedMedia postedMedia) {
        this.postedMedias.add(postedMedia);
        postedMedia.updatePost(this);
    }

    public void addMovieInPost(PostedMovie postedMovie) {
        this.postedMovies.add(postedMovie);
        postedMovie.updatePost(this);
    }

    //==생성 메소드==//
    public static Post createPost(Member member, String title, String content,
                                  List<PostedHashtag> postedHashtags, List<PostedMedia> postedMedia,
                                  List<PostedMovie> postedMovies, PublicOfPostStatus publicOfPostStatus) {
        Post post = new Post();
        post.updateMember(member);
        post.updateTitle(title);
        post.updateContent(content);
        post.updatePostedHashtag(postedHashtags);
        post.updateMedia(postedMedia);
        post.updatePublicOfPostStatus(publicOfPostStatus);
        post.updateMovieInPost(postedMovies);
        post.updateViewToZero();

        return post;
    }

    public static PostDto.PostBuilder buildPost() {
        return new PostDto.PostBuilder();
    }

    //==수정 메소드==//
    public void updatePublicOfPostStatus(PublicOfPostStatus publicStatus) {
        this.publicOfPostStatus = publicStatus;
    }

    public void updateViewToZero() {
        this.view = 0L;
    }

    public void updateView() {
        this.view = this.view + 1;
    }

    public void updateMedia(List<PostedMedia> postedMedias) {
        for (PostedMedia postedMedia : postedMedias) {
            this.addMedia(postedMedia);
        }
    }

    public void updatePostedHashtag(List<PostedHashtag> postedHashtags) {
        for (PostedHashtag postedHashTag : postedHashtags) {
            this.addPostedHashTag(postedHashTag);
        }
    }

    public void updateMovieInPost(List<PostedMovie> postedMovies) {
        for (PostedMovie postedMovie : postedMovies) {
            this.addMovieInPost(postedMovie);
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