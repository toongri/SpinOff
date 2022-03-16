package com.nameless.spin_off.entity.post;

import com.nameless.spin_off.dto.PostDto.PostBuilder;
import com.nameless.spin_off.entity.collection.CollectedPost;
import com.nameless.spin_off.entity.collection.Collection;
import com.nameless.spin_off.entity.comment.CommentInPost;
import com.nameless.spin_off.entity.enums.post.AuthorityOfPostStatus;
import com.nameless.spin_off.entity.enums.post.PublicOfPostStatus;
import com.nameless.spin_off.entity.hashtag.Hashtag;
import com.nameless.spin_off.entity.hashtag.PostedHashtag;
import com.nameless.spin_off.entity.listener.BaseTimeEntity;
import com.nameless.spin_off.entity.member.Member;
import com.nameless.spin_off.entity.movie.Movie;
import com.nameless.spin_off.exception.collection.AlreadyCollectedPostException;
import com.nameless.spin_off.exception.comment.NotExistCommentInPostException;
import com.nameless.spin_off.exception.post.AlreadyLikedPostException;
import com.nameless.spin_off.exception.post.AlreadyPostedHashtagException;
import com.nameless.spin_off.exception.post.OverContentOfPostException;
import com.nameless.spin_off.exception.post.OverTitleOfPostException;
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

import static com.nameless.spin_off.entity.enums.ContentsTimeEnum.VIEWED_BY_IP_MINUTE;
import static com.nameless.spin_off.entity.enums.post.PostContentLimitEnum.CONTENT_LENGTH_MAX;
import static com.nameless.spin_off.entity.enums.post.PostContentLimitEnum.TITLE_LENGTH_MAX;
import static com.nameless.spin_off.entity.enums.post.PostScoreEnum.*;

@Entity
@Getter
@ToString
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Post extends BaseTimeEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
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

    private List<Long> addAllCollectedPost(List<Collection> collections) {
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

    public void addAllPostedMedias(List<PostedMedia> postedMedias) {
        for (PostedMedia postedMedia : postedMedias) {
            this.addPostedMedia(postedMedia);
        }
    }

    //==생성 메소드==//
    public static Post createPost(Member member, String title, String content, String thumbnailUrl,
                                  List<Hashtag> hashtags, List<PostedMedia> postedMedias, List<Collection> collections,
                                  Movie movie, PublicOfPostStatus publicOfPostStatus)
            throws AlreadyPostedHashtagException, OverTitleOfPostException, OverContentOfPostException {
        Post post = new Post();
        member.addPost(post);

        if (title.length() > TITLE_LENGTH_MAX.getValue()) {
            throw new OverTitleOfPostException();
        }
        post.updateTitle(title);
        if (content.length() > CONTENT_LENGTH_MAX.getValue()) {
            throw new OverContentOfPostException();
        }
        post.updateContent(content);
        post.updateThumbnailUrl(thumbnailUrl);
        post.addAllPostedHashtagsByHashtags(hashtags);
        post.addAllPostedMedias(postedMedias);
        post.updatePublicOfPostStatus(publicOfPostStatus);
        post.updateCountToZero();
        post.updateAuthorityOfPostStatus(AuthorityOfPostStatus.C);


        if (!collections.isEmpty()) {
            post.addAllCollectedPost(collections);
        }

        if (movie != null)
            movie.addTaggedPosts(post);

        return post;
    }

    public static PostBuilder buildPost() {
        return new PostBuilder();
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
            return addAllCollectedPost(collections);
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

        return commentInPosts.stream().filter(comment -> comment.getId().equals(commentInPostId))
                .findAny().orElseThrow(NotExistCommentInPostException::new);

    }

    public void updateViewScore() {

        LocalDateTime currentTime = LocalDateTime.now();
        ViewedPostByIp viewedPostByIp;
        int j = 0, i = viewedPostByIps.size() - 1;
        double result = 0, total = 1 * POST_VIEW.getLatestScore();

        while (i > -1) {
            viewedPostByIp = viewedPostByIps.get(i);
            if (isInTimeViewedPost(currentTime, viewedPostByIp, j)) {
                result += 1;
                i--;
            } else {
                if (j == POST_VIEW.getScores().size() - 1) {
                    break;
                }
                total += POST_VIEW.getScores().get(j) * result;
                result = 0;
                j++;
            }
        }
        viewScore = (total + POST_VIEW.getScores().get(j) * result ) * POST_VIEW.getRate();
        updatePopularity();
    }

    public void updateLikeScore() {

        LocalDateTime currentTime = LocalDateTime.now();
        LikedPost likedPost;
        int j = 0, i = likedPosts.size() - 1;
        double result = 0, total = 1 * POST_LIKE.getLatestScore();

        while (i > -1) {
            likedPost = likedPosts.get(i);
            if (isInTimeLikedPost(currentTime, likedPost, j)) {
                result += 1;
                i--;
            } else {
                if (j == POST_LIKE.getScores().size() - 1) {
                    break;
                }
                total += POST_LIKE.getScores().get(j) * result;
                result = 0;
                j++;
            }
        }
        likeScore = (total + POST_LIKE.getScores().get(j) * result) * POST_LIKE.getRate();
        updatePopularity();
    }

    public void updateCommentScore() {

        LocalDateTime currentTime = LocalDateTime.now();
        CommentInPost commentInPost;
        int j = 0, i = commentInPosts.size() - 1;
        double result = 0, total = 1 * POST_COMMENT.getLatestScore();

        while (i > -1) {
            commentInPost = commentInPosts.get(i);
            if (isInTimeCommentInPost(currentTime, commentInPost, j)) {
                result += 1;
                i--;
            } else {
                if (j == POST_COMMENT.getScores().size() - 1) {
                    break;
                }
                total += POST_COMMENT.getScores().get(j) * result;
                result = 0;
                j++;
            }
        }
        commentScore = (total + POST_COMMENT.getScores().get(j) * result) * POST_COMMENT.getRate();
        updatePopularity();
    }

    public void updateCollectScore(int listSize) {

        LocalDateTime currentTime = LocalDateTime.now();
        CollectedPost collectedPost;
        int j = 0, i = collectedPosts.size() - 1;
        double result = 0, total = listSize * POST_COLLECT.getLatestScore();

        while (i > -1) {
            collectedPost = collectedPosts.get(i);
            if (isInTimeCollectedPost(currentTime, collectedPost, j)) {
                result += 1;
                i--;
            } else {
                if (j == POST_COLLECT.getScores().size() - 1) {
                    break;
                }
                total += POST_COLLECT.getScores().get(j) * result;
                result = 0;
                j++;
            }
        }
        collectionScore = (total + POST_COLLECT.getScores().get(j) * result) * POST_COLLECT.getRate();
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
                .between(views.get(views.size() - 1).getCreatedDate(), currentTime) >= VIEWED_BY_IP_MINUTE.getTime();
    }

    public Boolean isNotAlreadyMemberLikePost(Member member) {
        return this.likedPosts.stream().noneMatch(likedPost -> likedPost.getMember().equals(member));
    }

    public Boolean isNotAlreadyCollectedPosts(List<Collection> collections) {
        return this.collectedPosts.stream().noneMatch(collectedPost -> collections.contains(collectedPost.getCollection()));
    }

    private boolean isInTimeViewedPost(LocalDateTime currentTime, ViewedPostByIp viewedPostByIp, int j) {
        return ChronoUnit.DAYS
                .between(viewedPostByIp.getCreatedDate(), currentTime) >= POST_VIEW.getDays().get(j) &&
                ChronoUnit.DAYS
                        .between(viewedPostByIp.getCreatedDate(), currentTime) < POST_VIEW.getDays().get(j + 1);
    }

    private boolean isInTimeLikedPost(LocalDateTime currentTime, LikedPost likedPost, int j) {
        return ChronoUnit.DAYS
                .between(likedPost.getCreatedDate(), currentTime) >= POST_LIKE.getDays().get(j) &&
                ChronoUnit.DAYS
                        .between(likedPost.getCreatedDate(), currentTime) < POST_LIKE.getDays().get(j + 1);
    }

    private boolean isInTimeCollectedPost(LocalDateTime currentTime, CollectedPost collectedPost, int j) {
        return ChronoUnit.DAYS
                .between(collectedPost.getCreatedDate(), currentTime) >= POST_COLLECT.getDays().get(j) &&
                ChronoUnit.DAYS
                        .between(collectedPost.getCreatedDate(), currentTime) < POST_COLLECT.getDays().get(j + 1);
    }

    private boolean isInTimeCommentInPost(LocalDateTime currentTime, CommentInPost commentInPost, int j) {
        return ChronoUnit.DAYS
                .between(commentInPost.getCreatedDate(), currentTime) >= POST_COMMENT.getDays().get(j) &&
                ChronoUnit.DAYS
                        .between(commentInPost.getCreatedDate(), currentTime) < POST_COMMENT.getDays().get(j + 1);
    }
}