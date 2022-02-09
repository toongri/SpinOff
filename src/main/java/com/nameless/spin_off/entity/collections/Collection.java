package com.nameless.spin_off.entity.collections;

import com.nameless.spin_off.entity.comment.CommentInCollection;
import com.nameless.spin_off.entity.listener.BaseTimeEntity;
import com.nameless.spin_off.entity.member.Member;
import com.nameless.spin_off.entity.post.Post;
import com.nameless.spin_off.exception.collection.OverSearchCollectedPostException;
import com.nameless.spin_off.exception.collection.OverSearchFollowedCollectionException;
import com.nameless.spin_off.exception.collection.OverSearchLikedCollectionException;
import com.nameless.spin_off.exception.collection.OverSearchViewedCollectionByIpException;
import com.nameless.spin_off.exception.comment.NotSearchCommentInCollectionException;
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
    public void updateViewCount() {
        this.viewCount += 1;
        this.popularity += 1;
    }

    public void updateLikeCount() {
        this.likeCount += 1;
        this.popularity += 1;
    }

    public void updateCommentInCollectionCount() {
        this.commentCount += 1;
        this.popularity += 1;
    }

    public void updateFollowCount() {
        this.followCount += 1;
        this.popularity += 1;
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

    public CommentInCollection getParentCommentById(Long commentInCollectionId)
            throws NotSearchCommentInCollectionException {

        if (commentInCollectionId == null)
            return null;

        List<CommentInCollection> commentInCollection = commentInCollections.stream()
                .filter(comment -> comment.getId().equals(commentInCollectionId))
                .collect(Collectors.toList());

        if (commentInCollection.isEmpty()) {
            throw new NotSearchCommentInCollectionException();
        } else {
            return commentInCollection.get(0);
        }
    }
    //==조회 로직==//

    public Boolean isNotIpAlreadyView(String ip, LocalDateTime timeNow, Long minuteDuration )
            throws OverSearchViewedCollectionByIpException {

        List<ViewedCollectionByIp> viewed = viewedCollectionByIps.stream()
                .filter(viewedPostByIp -> viewedPostByIp.getIp().equals(ip) &&
                        ChronoUnit.MINUTES.between(viewedPostByIp.getCreatedDate(), timeNow) < minuteDuration)
                .collect(Collectors.toList());

        int size = viewed.size();

        if (size == 0)
            return true;
        else if (size == 1)
            return false;
        else
            throw new OverSearchViewedCollectionByIpException();

    }

    public Boolean isNotMemberAlreadyLikeCollection(Member member)
            throws OverSearchLikedCollectionException {

        List<LikedCollection> likedCollections = this.likedCollections.stream()
                .filter(likedCollection -> likedCollection.getMember().equals(member))
                .collect(Collectors.toList());

        int size = likedCollections.size();

        if (size == 0)
            return true;
        else if (size == 1)
            return false;
        else
            throw new OverSearchLikedCollectionException();
    }

    public Boolean isNotMemberAlreadyFollowCollection(Member member)
            throws OverSearchFollowedCollectionException {

        List<FollowedCollection> followedCollections = this.followedCollections.stream()
                .filter(followedCollection -> followedCollection.getMember().equals(member))
                .collect(Collectors.toList());

        int size = followedCollections.size();

        if (size == 0)
            return true;
        else if (size == 1)
            return false;
        else
            throw new OverSearchFollowedCollectionException();
    }

    public Boolean isNotAlreadyCollectedPost(Post post)
            throws OverSearchCollectedPostException {

        List<CollectedPost> collectedPosts = this.collectedPosts
                .stream().filter(collectedPost -> collectedPost.getPost().equals(post))
                .collect(Collectors.toList());

        int size = collectedPosts.size();

        if (size == 0)
            return true;
        else if (size == 1)
            return false;
        else
            throw new OverSearchCollectedPostException();
    }

}
