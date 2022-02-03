package com.nameless.spin_off.entity.post;

import com.nameless.spin_off.dto.PostDto;
import com.nameless.spin_off.entity.listener.BaseTimeEntity;
import com.nameless.spin_off.entity.member.Member;
import com.nameless.spin_off.entity.movie.Movie;
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

    @Enumerated(EnumType.STRING)
    @Column(name = "public_of_post_status")
    private PublicOfPostStatus publicOfPostStatus;

    @OneToMany(mappedBy = "post", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private List<CommentInPost> commentInPosts = new ArrayList<>();

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

    private Long viewCount;
    private Long likeCount;
    private Long commentCount;
    private Long collectionCount;

    //==연관관계 메소드==//

    public void addPostedMedia(PostedMedia postedMedia) {
        this.postedMedias.add(postedMedia);
        postedMedia.updatePost(this);
    }

    public void addViewedPostByIp(String ip) {
        ViewedPostByIp viewedPostByIp = ViewedPostByIp.createViewedPostByIp(ip);

        this.viewedPostByIps.add(viewedPostByIp);
        viewedPostByIp.updatePost(this);
        this.updateViewCount();
    }

    public void addVisitedPostByMember(Member member) {
        VisitedPostByMember visitedPostByMember = VisitedPostByMember.createVisitedPostByMember(member);

        this.visitedPostByMembers.add(visitedPostByMember);
        visitedPostByMember.updatePost(this);
    }

    public void addCommentByMemberAndContent(Member member, String content) {
        CommentInPost commentInPost = CommentInPost.createCommentInPost(member, content);

        this.commentInPosts.add(commentInPost);
        commentInPost.updatePost(this);
        this.updateCommentCount();
    }

    public void addLikedPostByMember(Member member) {
        LikedPost likedPost = LikedPost.createLikedPost(member);

        this.likedPosts.add(likedPost);
        likedPost.updatePost(this);
        this.updateLikeCount();
    }

    public void addPostedHashtagByHashtag(Hashtag hashtag) {
        PostedHashtag postedHashtag = PostedHashtag.createPostedHashtag(hashtag);

        this.postedHashtags.add(postedHashtag);
        postedHashtag.updatePost(this);
    }

    public void addPostedMovieByMovie(Movie movie) {
        PostedMovie postedMovie = PostedMovie.createPostedMovie(movie);

        this.postedMovies.add(postedMovie);
        postedMovie.updatePost(this);
    }

    //==생성 메소드==//
    public static Post createPost(Member member, String title, String content,
                                  List<Hashtag> hashtags, List<PostedMedia> postedMedias,
                                  List<Movie> movies, PublicOfPostStatus publicOfPostStatus) {
        Post post = new Post();
        post.updateMember(member);
        post.updateTitle(title);
        post.updateContent(content);
        post.updatePostedHashtagsByHashtags(hashtags);
        post.updatePostedMedias(postedMedias);
        post.updatePublicOfPostStatus(publicOfPostStatus);
        post.updatePostedMoviesByMovies(movies);
        post.updateViewCountToZero();
        post.updateCollectionCountToZero();
        post.updateCommentCountToZero();
        post.updateLikeCountToZero();

        return post;
    }

    public static PostDto.PostBuilder buildPost() {
        return new PostDto.PostBuilder();
    }

    //==수정 메소드==//
    public void updatePublicOfPostStatus(PublicOfPostStatus publicStatus) {
        this.publicOfPostStatus = publicStatus;
    }

    public void updateViewCountToZero() {
        this.viewCount = 0L;
    }

    public void updateViewCount() {
        this.viewCount = this.viewCount + 1;
    }

    public void updateLikeCountToZero() {
        this.likeCount = 0L;
    }

    public void updateLikeCount() {
        this.likeCount = this.likeCount + 1;
    }

    public void updateCommentCountToZero() {
        this.commentCount = 0L;
    }

    public void updateCommentCount() {
        this.commentCount = this.commentCount + 1;
    }

    public void updateCollectionCountToZero() {
        this.collectionCount = 0L;
    }

    public void updateCollectionCount() {
        this.collectionCount = this.collectionCount + 1;
    }

    public void updatePostedMedias(List<PostedMedia> postedMedias) {
        for (PostedMedia postedMedia : postedMedias) {
            this.addPostedMedia(postedMedia);
        }
    }

    public void updatePostedHashtagsByHashtags(List<Hashtag> hashtags) {
        for (Hashtag hashtag : hashtags) {
            this.addPostedHashtagByHashtag(hashtag);
        }
    }

    public void updatePostedMoviesByMovies(List<Movie> movies) {
        for (Movie movie : movies) {
            this.addPostedMovieByMovie(movie);
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