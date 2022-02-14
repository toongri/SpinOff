package com.nameless.spin_off.entity.collections;

import com.nameless.spin_off.entity.comment.CommentInCollection;
import com.nameless.spin_off.entity.listener.BaseTimeEntity;
import com.nameless.spin_off.entity.member.Member;
import com.nameless.spin_off.entity.post.Post;
import com.nameless.spin_off.exception.collection.*;
import com.nameless.spin_off.exception.comment.NotExistCommentInCollectionException;
import com.sun.istack.NotNull;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

import javax.persistence.*;
import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import static com.nameless.spin_off.StaticVariable.*;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Collection extends BaseTimeEntity {

    @Id @GeneratedValue
    @Column(name="collection_id")
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "member_id")
    @NotNull
    private Member member;

    private String title;

    private String content;

    @Enumerated(EnumType.STRING)
    @Column(name = "public_of_collection_status")
    private PublicOfCollectionStatus publicOfCollectionStatus;

    @OneToMany(mappedBy = "collection", cascade = CascadeType.ALL, fetch = FetchType.LAZY, orphanRemoval = true)
    private List<ViewedCollectionByIp> viewedCollectionByIps = new ArrayList<>();

    @OneToMany(mappedBy = "collection", cascade = CascadeType.ALL, fetch = FetchType.LAZY, orphanRemoval = true)
    private List<VisitedCollectionByMember> visitedCollectionByMembers = new ArrayList<>();

    @OneToMany(mappedBy = "collection", cascade = CascadeType.ALL, fetch = FetchType.LAZY, orphanRemoval = true)
    private List<LikedCollection> likedCollections = new ArrayList<>();

    @OneToMany(mappedBy = "collection", cascade = CascadeType.ALL, fetch = FetchType.LAZY, orphanRemoval = true)
    private List<FollowedCollection> followedCollections = new ArrayList<>();

    @OneToMany(mappedBy = "collection", cascade = CascadeType.ALL, fetch = FetchType.LAZY, orphanRemoval = true)
    private List<CollectedPost> collectedPosts = new ArrayList<>();

    @OneToMany(mappedBy = "collection", cascade = CascadeType.ALL, fetch = FetchType.LAZY, orphanRemoval = true)
    private List<CommentInCollection> commentInCollections = new ArrayList<>();

    private Long viewCount;
    private Long likeCount;
    private Long commentCount;
    private Long followCount;
    private Long popularity;

    //==연관관계 메소드==//

    public void addViewedCollectionByIp(String ip) {
        ViewedCollectionByIp viewedCollectionByIp = ViewedCollectionByIp.createViewedCollectionByIp(ip);

        this.viewedCollectionByIps.add(viewedCollectionByIp);
        viewedCollectionByIp.updateCollections(this);
        this.updateViewCount();
    }

    public void addVisitedCollectionByMember(Member member) {
        VisitedCollectionByMember visitedCollectionByMember = VisitedCollectionByMember.createVisitedCollectionByMember(member);

        this.visitedCollectionByMembers.add(visitedCollectionByMember);
        visitedCollectionByMember.updateCollections(this);
    }

    public void addLikedCollectionByMember(Member member) {
        LikedCollection likedCollection = LikedCollection.createLikedCollection(member);

        this.likedCollections.add(likedCollection);
        likedCollection.updateCollections(this);
        this.updateLikeCount();
    }

    public void addFollowedCollectionByMember(Member member) {
        FollowedCollection followedCollection = FollowedCollection.createFollowedCollection(member);

        this.followedCollections.add(followedCollection);
        followedCollection.updateCollections(this);
        this.updateFollowCount();
    }

    public void addCollectedPostByPost(Post post) {
        CollectedPost collectedPost = CollectedPost.createCollectedPost(post);

        this.collectedPosts.add(collectedPost);
        collectedPost.updateCollections(this);
        post.updateCollectionCount();
    }

    public void addCommentInCollection(CommentInCollection commentInCollection) {
        this.commentInCollections.add(commentInCollection);
        commentInCollection.updateCollection(this);
        this.updateCommentInCollectionCount();
    }

    //==생성 메소드==//
    public static Collection createCollection(Member member, String title,
                                              String content, PublicOfCollectionStatus publicOfCollectionStatus) {

        Collection collection = new Collection();
        collection.updateMember(member);
        collection.updateTitle(title);
        collection.updateContent(content);
        collection.updatePublicOfCollectionStatus(publicOfCollectionStatus);
        collection.updateCountToZero();
        return collection;
    }

    public static Collection createDefaultCollection(Member member) {

        final String DEFAULT_COLLECTION_TITLE = "나중에 볼 컬렉션";
        final String DEFAULT_COLLECTION_CONTENT = "";
        final PublicOfCollectionStatus DEFAULT_COLLECTION_PUBLIC_STATUS = PublicOfCollectionStatus.PRIVATE;

        Collection collection = new Collection();
        collection.updateMember(member);
        collection.updateTitle(DEFAULT_COLLECTION_TITLE);
        collection.updateContent(DEFAULT_COLLECTION_CONTENT);
        collection.updatePublicOfCollectionStatus(DEFAULT_COLLECTION_PUBLIC_STATUS);
        collection.updateCountToZero();

        return collection;

    }
    public static Collection createDocentCollection(Member member) {

        final String DOCENT_COLLECTION_TITLE = "도슨트 컬렉션";
        final String DOCENT_COLLECTION_CONTENT = "";
        final PublicOfCollectionStatus DOCENT_COLLECTION_PUBLIC_STATUS = PublicOfCollectionStatus.PUBLIC;

        Collection collection = new Collection();
        collection.updateMember(member);
        collection.updateTitle(DOCENT_COLLECTION_TITLE);
        collection.updateContent(DOCENT_COLLECTION_CONTENT);
        collection.updatePublicOfCollectionStatus(DOCENT_COLLECTION_PUBLIC_STATUS);
        collection.updateCountToZero();

        return collection;

    }

    //==수정 메소드==//
    public void updateCountToZero() {
        this.viewCount = 0L;
        this.likeCount = 0L;
        this.commentCount = 0L;
        this.followCount = 0L;
        this.popularity = 0L;
    }

    public void updatePopularity() {
        this.popularity = viewCount + likeCount + commentCount + followCount;
    }

    public void updateViewCount() {
        LocalDateTime currentTime = LocalDateTime.now();

        this.viewCount = viewedCollectionByIps.stream()
                .filter(viewedCollectionByIp -> ChronoUnit.DAYS
                        .between(viewedCollectionByIp.getCreatedDate(), currentTime) < COLLECTION_VIEWED_COUNT_DAYS)
                .count();

        updatePopularity();
    }

    public void updateLikeCount() {
        LocalDateTime currentTime = LocalDateTime.now();

        this.likeCount = likedCollections.stream()
                .filter(likedCollection -> ChronoUnit.DAYS
                        .between(likedCollection.getCreatedDate(), currentTime) < COLLECTION_LIKED_COUNT_DAYS)
                .count();

        updatePopularity();
    }

    public void updateCommentInCollectionCount() {
        LocalDateTime currentTime = LocalDateTime.now();

        this.commentCount = commentInCollections.stream()
                .filter(commentInCollection -> ChronoUnit.DAYS
                        .between(commentInCollection.getCreatedDate(), currentTime) < COLLECTION_LIKED_COUNT_DAYS)
                .count();

        updatePopularity();
    }

    public void updateFollowCount() {
        LocalDateTime currentTime = LocalDateTime.now();

        this.followCount = followedCollections.stream()
                .filter(followedCollection -> ChronoUnit.DAYS
                        .between(followedCollection.getCreatedDate(), currentTime) < COLLECTION_LIKED_COUNT_DAYS)
                .count();

        updatePopularity();
    }

    private void updatePublicOfCollectionStatus(PublicOfCollectionStatus publicOfCollectionStatus) {
        this.publicOfCollectionStatus = publicOfCollectionStatus;
    }

    private void updateContent(String content) {
        this.content = content;
    }

    private void updateTitle(String title) {
        this.title = title;
    }

    private void updateMember(Member member) {
        this.member = member;
    }

    //==비즈니스 로직==//

    public void insertCollectedPostByPost(Post post) throws AlreadyCollectedPostException {

        if (isNotAlreadyCollectedPost(post)) {
            addCollectedPostByPost(post);
        } else {
            throw new AlreadyCollectedPostException();
        }
    }

    public void insertFollowedCollectionByMember(Member member) throws AlreadyFollowedCollectionException {

        if (isNotAlreadyMemberFollowCollection(member)) {
            addFollowedCollectionByMember(member);
        } else {
            throw new AlreadyFollowedCollectionException();
        }
    }

    public boolean insertViewedCollectionByIp(String ip) {

        if (isNotAlreadyIpView(ip)) {
            addViewedCollectionByIp(ip);
            return true;
        } else {
            return false;
        }
    }

    public void insertLikedCollectionByMember(Member member) throws AlreadyLikedCollectionException {

        if (isNotAlreadyMemberLikeCollection(member)) {
            addLikedCollectionByMember(member);
        } else {
            throw new AlreadyLikedCollectionException();
        }
    }

    public CommentInCollection getParentCommentById(Long commentInCollectionId)
            throws NotExistCommentInCollectionException {

        if (commentInCollectionId == null)
            return null;

        List<CommentInCollection> commentInCollection = commentInCollections.stream()
                .filter(comment -> comment.getId().equals(commentInCollectionId))
                .collect(Collectors.toList());

        if (commentInCollection.isEmpty()) {
            throw new NotExistCommentInCollectionException();
        } else {
            return commentInCollection.get(0);
        }
    }
    //==조회 로직==//

    public Boolean isNotAlreadyIpView(String ip) {
        return viewedCollectionByIps.stream()
                .noneMatch(viewedCollectionByIp -> viewedCollectionByIp.getIp().equals(ip) &&
                        ChronoUnit.MINUTES.between(viewedCollectionByIp.getCreatedDate(), LocalDateTime.now())
                                < VIEWED_BY_IP_MINUTE);

    }

    public Boolean isNotAlreadyMemberLikeCollection(Member member) {
        return this.likedCollections.stream().noneMatch(likedCollection -> likedCollection.getMember().equals(member));
    }

    public Boolean isNotAlreadyMemberFollowCollection(Member member) {
        return this.followedCollections.stream()
                .noneMatch(followedCollection -> followedCollection.getMember().equals(member));
    }

    public Boolean isNotAlreadyCollectedPost(Post post) {
        return this.collectedPosts.stream().noneMatch(collectedPost -> collectedPost.getPost().equals(post));
    }
}
