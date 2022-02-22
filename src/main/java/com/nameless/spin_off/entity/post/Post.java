package com.nameless.spin_off.entity.post;

import com.nameless.spin_off.dto.PostDto;
import com.nameless.spin_off.entity.collection.CollectedPost;
import com.nameless.spin_off.entity.collection.Collection;
import com.nameless.spin_off.entity.comment.CommentInPost;
import com.nameless.spin_off.entity.hashtag.Hashtag;
import com.nameless.spin_off.entity.hashtag.PostedHashtag;
import com.nameless.spin_off.entity.listener.BaseTimeEntity;
import com.nameless.spin_off.entity.member.Member;
import com.nameless.spin_off.entity.movie.Movie;
import com.nameless.spin_off.exception.collection.AlreadyCollectedPostException;
import com.nameless.spin_off.exception.comment.NotExistCommentInPostException;
import com.nameless.spin_off.exception.post.*;
import com.sun.istack.NotNull;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.ToString;

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
    @NotNull
    private PublicOfPostStatus publicOfPostStatus;

    @Enumerated(EnumType.STRING)
    @Column(name = "authority_of_post_status")
    @NotNull
    private AuthorityOfPostStatus authorityOfPostStatus;

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

    private Double viewScore;
    private Double likeScore;
    private Double commentScore;
    private Double collectionScore;
    private Double popularity;

    //==연관관계 메소드==//

    public void addPostedMedia(PostedMedia postedMedia) {
        this.postedMedias.add(postedMedia);
        postedMedia.updatePost(this);
    }

    private List<Long> addCollectedPosts(List<Collection> collections) {
        this.updateCollectScore(collections.size());
        List<CollectedPost> collectedPosts = new ArrayList<>();

        for (Collection collection : collections) {
            CollectedPost collectedPost = CollectedPost.createCollectedPost(collection, this);
            collection.addCollectedPost(collectedPost);
            collectedPosts.add(collectedPost);
        }
        this.collectedPosts.addAll(collectedPosts);

        return collectedPosts.stream().map(CollectedPost::getId).collect(Collectors.toList());
    }

    private Long addViewedPostByIp(String ip) {
        ViewedPostByIp viewedPostByIp = ViewedPostByIp.createViewedPostByIp(ip, this);

        this.updateViewScore();
        this.viewedPostByIps.add(viewedPostByIp);

        return viewedPostByIp.getId();
    }

    public void addVisitedPostByMember(Member member) {
        VisitedPostByMember visitedPostByMember = VisitedPostByMember.createVisitedPostByMember(member, this);

        this.visitedPostByMembers.add(visitedPostByMember);
    }

    public void addCommentInPost(CommentInPost commentInPost) {
        this.updateCommentScore();
        this.commentInPosts.add(commentInPost);
        commentInPost.updatePost(this);
    }

    private Long addLikedPostByMember(Member member) {
        LikedPost likedPost = LikedPost.createLikedPost(member, this);

        this.updateLikeScore();
        this.likedPosts.add(likedPost);

        return likedPost.getId();
    }

    public void addPostedHashtagByHashtag(Hashtag hashtag) throws AlreadyPostedHashtagException {
        PostedHashtag postedHashtag = PostedHashtag.createPostedHashtag(hashtag, this);

        if (!this.postedHashtags.add(postedHashtag)) {
            throw new AlreadyPostedHashtagException();
        }
        hashtag.addTaggedPosts(postedHashtag);
    }

    public void addAllPostedHashtagsByHashtags(List<Hashtag> hashtags) throws AlreadyPostedHashtagException {
        for (Hashtag hashtag : hashtags) {
            this.addPostedHashtagByHashtag(hashtag);
        }
    }

    //==생성 메소드==//
    public static Post createPost(Member member, String title, String content, String thumbnailUrl,
                                  List<Hashtag> hashtags, List<PostedMedia> postedMedias,
                                  Movie movie, PublicOfPostStatus publicOfPostStatus)
            throws AlreadyPostedHashtagException, AlreadyPAuthorityOfPostStatusException,
            OverTitleOfPostException, OverContentOfPostException {
        Post post = new Post();
        post.updateMember(member);

        if (title.length() > 100) {
            throw new OverTitleOfPostException();
        }
        post.updateTitle(title);
        if (content.length() > 500) {
            throw new OverContentOfPostException();
        }
        post.updateContent(content);
        post.updateThumbnailUrl(thumbnailUrl);
        post.addAllPostedHashtagsByHashtags(hashtags);
        post.updatePostedMedias(postedMedias);
        post.updatePublicOfPostStatus(publicOfPostStatus);
        post.updateCountToZero();
        post.updateAuthorityOfPostStatus(AuthorityOfPostStatus.NORMAL);

        if (movie != null)
            movie.addTaggedPosts(post);

        return post;
    }

    public static PostDto.PostBuilder buildPost() {
        return new PostDto.PostBuilder();
    }

    //==수정 메소드==//

    public void updateCountToZero() {
        this.viewScore = 0.0;
        this.likeScore = 0.0;
        this.commentScore = 0.0;
        this.collectionScore = 0.0;
        this.popularity = 0.0;
    }

    public void updatePopularity() {
        popularity = viewScore + likeScore + commentScore + collectionScore;
    }

    public void updatePublicOfPostStatus(PublicOfPostStatus publicStatus) {
        this.publicOfPostStatus = publicStatus;
    }

    public void updatePostedMedias(List<PostedMedia> postedMedias) {
        for (PostedMedia postedMedia : postedMedias) {
            this.addPostedMedia(postedMedia);
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

    public void updateAuthorityOfPostStatus(AuthorityOfPostStatus authorityOfPostStatus) {
        this.authorityOfPostStatus = authorityOfPostStatus;
    }

    //==비즈니스 로직==//
    public List<Long> insertCollectedPostByCollections(List<Collection> collections) throws AlreadyCollectedPostException {

        if (isNotAlreadyCollectedPosts(collections)) {
            return addCollectedPosts(collections);
        } else {
            throw new AlreadyCollectedPostException();
        }
    }

    public Long insertViewedPostByIp(String ip) {

        if (isNotAlreadyIpView(ip)) {
            return addViewedPostByIp(ip);
        } else {
            return -1L;
        }
    }

    public Long insertLikedPostByMember(Member member) throws AlreadyLikedPostException {

        if (isNotAlreadyMemberLikePost(member)) {
            return addLikedPostByMember(member);
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

    public void updateViewScore() {

        LocalDateTime currentTime = LocalDateTime.now();
        ViewedPostByIp viewedPostByIp;
        int j = 0, i = viewedPostByIps.size() - 1;
        double result = 0, total = 1 * POST_VIEW_COUNT_SCORES.get(0);

        while (i > -1) {
            viewedPostByIp = viewedPostByIps.get(i);
            if (isInTimeViewedPost(currentTime, viewedPostByIp, j)) {
                result += 1;
                i--;
            } else {
                if (j == POST_VIEW_COUNT_SCORES.size() - 1) {
                    break;
                }
                total += POST_VIEW_COUNT_SCORES.get(j) * result;
                result = 0;
                j++;
            }
        }
        viewScore = (total + POST_VIEW_COUNT_SCORES.get(j) * result ) * POST_SCORE_VIEW_RATES;
        updatePopularity();
    }

    public void updateLikeScore() {

        LocalDateTime currentTime = LocalDateTime.now();
        LikedPost likedPost;
        int j = 0, i = likedPosts.size() - 1;
        double result = 0, total = 1 * POST_LIKE_COUNT_SCORES.get(0);

        while (i > -1) {
            likedPost = likedPosts.get(i);
            if (isInTimeLikedPost(currentTime, likedPost, j)) {
                result += 1;
                i--;
            } else {
                if (j == POST_LIKE_COUNT_SCORES.size() - 1) {
                    break;
                }
                total += POST_LIKE_COUNT_SCORES.get(j) * result;
                result = 0;
                j++;
            }
        }
        likeScore = (total + POST_LIKE_COUNT_SCORES.get(j) * result) * POST_SCORE_LIKE_RATES;
        updatePopularity();
    }

    public void updateCommentScore() {

        LocalDateTime currentTime = LocalDateTime.now();
        CommentInPost commentInPost;
        int j = 0, i = commentInPosts.size() - 1;
        double result = 0, total = 1 * POST_COMMENT_COUNT_SCORES.get(0);

        while (i > -1) {
            commentInPost = commentInPosts.get(i);
            if (isInTimeCommentInPost(currentTime, commentInPost, j)) {
                result += 1;
                i--;
            } else {
                if (j == POST_COMMENT_COUNT_SCORES.size() - 1) {
                    break;
                }
                total += POST_COMMENT_COUNT_SCORES.get(j) * result;
                result = 0;
                j++;
            }
        }
        commentScore = (total + POST_COMMENT_COUNT_SCORES.get(j) * result) * POST_SCORE_COMMENT_RATES;
        updatePopularity();
    }

    public void updateCollectScore(int listSize) {

        LocalDateTime currentTime = LocalDateTime.now();
        CollectedPost collectedPost;
        int j = 0, i = collectedPosts.size() - 1;
        double result = 0, total = listSize * POST_COLLECT_COUNT_SCORES.get(0);

        while (i > -1) {
            collectedPost = collectedPosts.get(i);
            if (isInTimeCollectedPost(currentTime, collectedPost, j)) {
                result += 1;
                i--;
            } else {
                if (j == POST_COLLECT_COUNT_SCORES.size() - 1) {
                    break;
                }
                total += POST_COLLECT_COUNT_SCORES.get(j) * result;
                result = 0;
                j++;
            }
        }
        collectionScore = (total + POST_COLLECT_COUNT_SCORES.get(j) * result) * POST_SCORE_COLLECT_RATES;
        updatePopularity();
    }

    //==조회 로직==//

    public Integer getViewSize() {
        return viewedPostByIps.size();
    }

    public Boolean isNotAlreadyIpView(String ip) {

        LocalDateTime currentTime = LocalDateTime.now();

        List<ViewedPostByIp> views = viewedPostByIps.stream()
                .filter(viewedPostByIp -> viewedPostByIp.getIp().equals(ip))
                .collect(Collectors.toList());

        if (views.isEmpty()) {
            return true;
        }
        return ChronoUnit.MINUTES
                .between(views.get(views.size() - 1).getCreatedDate(), currentTime) >= VIEWED_BY_IP_MINUTE;
    }

    public Boolean isNotAlreadyMemberLikePost(Member member) {
        return this.likedPosts.stream().noneMatch(likedPost -> likedPost.getMember().equals(member));
    }

    public Boolean isNotAlreadyCollectedPosts(List<Collection> collections) {
        return this.collectedPosts.stream().noneMatch(collectedPost -> collections.contains(collectedPost.getCollection()));
    }

    private boolean isInTimeViewedPost(LocalDateTime currentTime, ViewedPostByIp viewedPostByIp, int j) {
        return ChronoUnit.DAYS
                .between(viewedPostByIp.getCreatedDate(), currentTime) >= POST_VIEW_COUNT_DAYS.get(j) &&
                ChronoUnit.DAYS
                        .between(viewedPostByIp.getCreatedDate(), currentTime) < POST_VIEW_COUNT_DAYS.get(j + 1);
    }

    private boolean isInTimeLikedPost(LocalDateTime currentTime, LikedPost likedPost, int j) {
        return ChronoUnit.DAYS
                .between(likedPost.getCreatedDate(), currentTime) >= POST_LIKE_COUNT_DAYS.get(j) &&
                ChronoUnit.DAYS
                        .between(likedPost.getCreatedDate(), currentTime) < POST_LIKE_COUNT_DAYS.get(j + 1);
    }

    private boolean isInTimeCollectedPost(LocalDateTime currentTime, CollectedPost collectedPost, int j) {
        return ChronoUnit.DAYS
                .between(collectedPost.getCreatedDate(), currentTime) >= POST_COLLECT_COUNT_DAYS.get(j) &&
                ChronoUnit.DAYS
                        .between(collectedPost.getCreatedDate(), currentTime) < POST_COLLECT_COUNT_DAYS.get(j + 1);
    }

    private boolean isInTimeCommentInPost(LocalDateTime currentTime, CommentInPost commentInPost, int j) {
        return ChronoUnit.DAYS
                .between(commentInPost.getCreatedDate(), currentTime) >= POST_COMMENT_COUNT_DAYS.get(j) &&
                ChronoUnit.DAYS
                        .between(commentInPost.getCreatedDate(), currentTime) < POST_COMMENT_COUNT_DAYS.get(j + 1);
    }
}