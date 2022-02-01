package com.nameless.spin_off.entity.collections;

import com.nameless.spin_off.entity.listener.BaseTimeEntity;
import com.nameless.spin_off.entity.member.Member;
import com.sun.istack.NotNull;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

import javax.persistence.*;
import java.util.ArrayList;
import java.util.List;

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

    private Long view;

    @Enumerated(EnumType.STRING)
    @Column(name = "public_of_collection_status")
    private PublicOfCollectionStatus publicOfCollectionStatus;

    @OneToMany(mappedBy = "collection", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private List<ViewedCollectionByIp> viewedCollectionByIps = new ArrayList<>();

    @OneToMany(mappedBy = "collection", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private List<VisitedCollectionByMember> visitedCollectionByMembers = new ArrayList<>();

    @OneToMany(mappedBy = "collection", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private List<LikedCollection> likedCollections = new ArrayList<>();

    @OneToMany(mappedBy = "collection", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private List<FollowedCollection> followedCollections = new ArrayList<>();

    @OneToMany(mappedBy = "collection", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private List<CollectedPost> collectedPosts = new ArrayList<>();

    //==연관관계 메소드==//
    public void addViewedCollectionByIp(ViewedCollectionByIp viewedCollectionByIp) {
        this.viewedCollectionByIps.add(viewedCollectionByIp);
        viewedCollectionByIp.updateCollections(this);
    }

    public void addVisitedCollectionByMember(VisitedCollectionByMember visitedCollectionByMember) {
        this.visitedCollectionByMembers.add(visitedCollectionByMember);
        visitedCollectionByMember.updateCollections(this);
    }

    public void addLikedCollection(LikedCollection likedCollection) {
        this.likedCollections.add(likedCollection);
        likedCollection.updateCollections(this);
    }

    public void addFollowedCollection(FollowedCollection followedCollection) {
        this.followedCollections.add(followedCollection);
        followedCollection.updateCollections(this);
    }
    public void addCollectedPost(CollectedPost collectedPost) {
        this.collectedPosts.add(collectedPost);
        collectedPost.updateCollections(this);
    }

    //==생성 메소드==//
    public static Collection createCollection(Member member, String title,
                                              String content, PublicOfCollectionStatus publicOfCollectionStatus) {

        Collection collection = new Collection();
        collection.updateMember(member);
        collection.updateTitle(title);
        collection.updateContent(content);
        collection.updatePublicOfCollectionStatus(publicOfCollectionStatus);
        collection.updateViewToZero();
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
        collection.updateViewToZero();

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
        collection.updateViewToZero();

        return collection;

    }

    //==수정 메소드==//

    public void updateViewToZero() {
        this.view = 0L;
    }

    public void updateView() {
        this.view = this.view + 1;
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

    //==조회 로직==//

}
