package com.nameless.spin_off.entity.post;

import com.nameless.spin_off.StaticVariable;
import com.nameless.spin_off.dto.PostDto;
import com.nameless.spin_off.entity.collections.CollectedPost;
import com.nameless.spin_off.entity.comment.CommentInPost;
import com.nameless.spin_off.entity.hashtag.Hashtag;
import com.nameless.spin_off.entity.hashtag.PostedHashtag;
import com.nameless.spin_off.entity.listener.BaseTimeEntity;
import com.nameless.spin_off.entity.member.Member;
import com.nameless.spin_off.entity.movie.Movie;
import com.nameless.spin_off.exception.comment.NotExistCommentInPostException;
import com.nameless.spin_off.exception.post.AlreadyLikedPostException;
import com.nameless.spin_off.exception.post.AlreadyPostedHashtagException;
import com.sun.istack.NotNull;
import lombok.*;

import javax.persistence.*;
import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

import static com.nameless.spin_off.StaticVariable.*;

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

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "movie_id")
    private Movie movie;

    private String title;
    private String content;
    private String thumbnailUrl;

    @Enumerated(EnumType.STRING)
    @Column(name = "public_of_post_status")
    private PublicOfPostStatus publicOfPostStatus;

    @OneToMany(mappedBy = "post", cascade = CascadeType.ALL, fetch = FetchType.LAZY, orphanRemoval = true)
    private List<CommentInPost> commentInPosts = new ArrayList<>();

    @OneToMany(mappedBy = "post", cascade = CascadeType.ALL, fetch = FetchType.LAZY, orphanRemoval = true)
    private List<LikedPost> likedPosts = new ArrayList<>();

    @OneToMany(mappedBy = "post", cascade = CascadeType.ALL, fetch = FetchType.LAZY, orphanRemoval = true)
    private Set<PostedHashtag> postedHashtags = new HashSet<>();

    @OneToMany(mappedBy = "post", cascade = CascadeType.ALL, fetch = FetchType.LAZY, orphanRemoval = true)
    private List<PostedMedia> postedMedias = new ArrayList<>();

    @OneToMany(mappedBy = "post", cascade = CascadeType.ALL, fetch = FetchType.LAZY, orphanRemoval = true)
    private List<ViewedPostByIp> viewedPostByIps = new ArrayList<>();

    @OneToMany(mappedBy = "post", cascade = CascadeType.ALL, fetch = FetchType.LAZY, orphanRemoval = true)
    private List<VisitedPostByMember> visitedPostByMembers = new ArrayList<>();

    @OneToMany(mappedBy = "post", fetch = FetchType.LAZY)
    private List<CollectedPost> collectedPosts = new ArrayList<>();

    private Long viewCount;
    private Long likeCount;
    private Long commentCount;
    private Long collectionCount;
    private Long popularity;

    //==연관관계 메소드==//

    public void addPostedMedia(PostedMedia postedMedia) {
        this.postedMedias.add(postedMedia);
        postedMedia.updatePost(this);
    }

    public void addCollectedPost(CollectedPost collectedPost) {
        this.updateCollectionCount();
        this.collectedPosts.add(collectedPost);
    }

    public void addViewedPostByIp(String ip) {
        ViewedPostByIp viewedPostByIp = ViewedPostByIp.createViewedPostByIp(ip);

        this.updateViewCount();
        this.viewedPostByIps.add(viewedPostByIp);
        viewedPostByIp.updatePost(this);
    }

    public void addVisitedPostByMember(Member member) {
        VisitedPostByMember visitedPostByMember = VisitedPostByMember.createVisitedPostByMember(member);

        this.visitedPostByMembers.add(visitedPostByMember);
        visitedPostByMember.updatePost(this);
    }

    public void addCommentInPost(CommentInPost commentInPost) {
        this.updateCommentInPostCount();
        this.commentInPosts.add(commentInPost);
        commentInPost.updatePost(this);
    }

    public void addLikedPostByMember(Member member) {
        LikedPost likedPost = LikedPost.createLikedPost(member);

        this.updateLikeCount();
        this.likedPosts.add(likedPost);
        likedPost.updatePost(this);
    }

    public void addPostedHashtagByHashtag(Hashtag hashtag) throws AlreadyPostedHashtagException {
        PostedHashtag postedHashtag = PostedHashtag.createPostedHashtag(hashtag);

        if (!this.postedHashtags.add(postedHashtag)) {
            throw new AlreadyPostedHashtagException();
        }
        postedHashtag.updatePost(this);
    }

    //==생성 메소드==//
    public static Post createPost(Member member, String title, String content, String thumbnailUrl,
                                  List<Hashtag> hashtags, List<PostedMedia> postedMedias,
                                  Movie movie, PublicOfPostStatus publicOfPostStatus) throws AlreadyPostedHashtagException {
        Post post = new Post();
        post.updateMember(member);
        post.updateTitle(title);
        post.updateThumbnailUrl(thumbnailUrl);
        post.updateContent(content);
        post.updatePostedHashtagsByHashtags(hashtags);
        post.updatePostedMedias(postedMedias);
        post.updatePublicOfPostStatus(publicOfPostStatus);
        post.updateCountToZero();

        if (movie != null)
            movie.addTaggedPosts(post);

        return post;
    }

    public static PostDto.PostBuilder buildPost() {
        return new PostDto.PostBuilder();
    }

    //==수정 메소드==//

    public void updateCountToZero() {
        this.viewCount = 0L;
        this.likeCount = 0L;
        this.commentCount = 0L;
        this.collectionCount = 0L;
        this.popularity = 0L;
    }

    public void updatePopularity() {
        this.popularity = viewCount + likeCount + commentCount + collectionCount;
    }

    public void updatePublicOfPostStatus(PublicOfPostStatus publicStatus) {
        this.publicOfPostStatus = publicStatus;
    }

    public void updateViewCount() {
        LocalDateTime currentTime = LocalDateTime.now();

        this.viewCount = viewedPostByIps.stream()
                .filter(viewedPostByIp -> ChronoUnit.DAYS
                        .between(viewedPostByIp.getCreatedDate(), currentTime) < POST_VIEW_COUNT_DAYS)
                .count() + 1;

        updatePopularity();
    }

    public void updateLikeCount() {
        LocalDateTime currentTime = LocalDateTime.now();

        this.likeCount = likedPosts.stream()
                .filter(likedPost -> ChronoUnit.DAYS
                        .between(likedPost.getCreatedDate(), currentTime) < POST_LIKE_COUNT_DAYS)
                .count() + 1;

        updatePopularity();
    }

    public void updateCommentInPostCount() {
        LocalDateTime currentTime = LocalDateTime.now();

        this.commentCount = commentInPosts.stream()
                .filter(commentInPost -> ChronoUnit.DAYS
                        .between(commentInPost.getCreatedDate(), currentTime) < POST_COMMENT_COUNT_DAYS)
                .count() + 1;

        updatePopularity();
    }

    public void updateCollectionCount() {
        LocalDateTime currentTime = LocalDateTime.now();

        this.collectionCount = collectedPosts.stream()
                .filter(collectedPost -> ChronoUnit.DAYS
                        .between(collectedPost.getCreatedDate(), currentTime) < POST_COLLECTION_COUNT_DAYS)
                .count() + 1;

        updatePopularity();
    }

    public void updatePostedMedias(List<PostedMedia> postedMedias) {
        for (PostedMedia postedMedia : postedMedias) {
            this.addPostedMedia(postedMedia);
        }
    }

    public void updatePostedHashtagsByHashtags(List<Hashtag> hashtags) throws AlreadyPostedHashtagException {
        for (Hashtag hashtag : hashtags) {
            this.addPostedHashtagByHashtag(hashtag);
        }
    }

    public void updateThumbnailUrl(String thumbnailUrl) {
        this.thumbnailUrl = thumbnailUrl;
    }

    public void updateMovie(Movie movie) {
        this.movie = movie;
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

    public boolean insertViewedPostByIp(String ip) {

        if (isNotAlreadyIpView(ip)) {
            addViewedPostByIp(ip);
            return true;
        } else {
            return false;
        }
    }

    public void insertLikedPostByMember(Member member) throws AlreadyLikedPostException {

        if (isNotAlreadyMemberLikePost(member)) {
            addLikedPostByMember(member);
        } else {
            throw new AlreadyLikedPostException();
        }
    }

    public CommentInPost getParentCommentById(Long commentInPostId) throws NotExistCommentInPostException {

        if (commentInPostId == null)
            return null;

        List<CommentInPost> commentInPost = commentInPosts.stream()
                .filter(comment -> comment.getId().equals(commentInPostId))
                .collect(Collectors.toList());

        if (commentInPost.isEmpty()) {
            throw new NotExistCommentInPostException();
        } else {
            return commentInPost.get(0);
        }
    }

    //==조회 로직==//

    public Boolean isNotAlreadyIpView(String ip) {
        return viewedPostByIps.stream()
                .noneMatch(viewedPostByIp -> viewedPostByIp.getIp().equals(ip) &&
                        ChronoUnit.MINUTES.between(viewedPostByIp.getCreatedDate(), LocalDateTime.now())
                                < StaticVariable.VIEWED_BY_IP_MINUTE);
    }

    public Boolean isNotAlreadyMemberLikePost(Member member) {
        return this.likedPosts.stream().noneMatch(likedPost -> likedPost.getMember().equals(member));
    }

}