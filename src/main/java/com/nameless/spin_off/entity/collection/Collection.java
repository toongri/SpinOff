package com.nameless.spin_off.entity.collection;

import com.nameless.spin_off.entity.comment.CommentInCollection;
import com.nameless.spin_off.entity.member.Member;
import com.nameless.spin_off.entity.post.Post;
import com.nameless.spin_off.enums.ErrorEnum;
import com.nameless.spin_off.enums.collection.PublicOfCollectionStatus;
import com.nameless.spin_off.exception.collection.*;
import com.nameless.spin_off.exception.comment.NotExistCommentInCollectionException;
import com.sun.istack.NotNull;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import javax.persistence.*;
import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import static com.nameless.spin_off.enums.ContentsTimeEnum.VIEWED_BY_IP_MINUTE;
import static com.nameless.spin_off.enums.collection.CollectionCondition.CONTENT;
import static com.nameless.spin_off.enums.collection.CollectionCondition.TITLE;
import static com.nameless.spin_off.enums.collection.CollectionScoreEnum.*;

@Entity
@Getter
@EntityListeners(AuditingEntityListener.class)
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Collection {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name="collection_id")
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "member_id")
    @NotNull
    private Member member;

    private String title;

    private String content;
    private String firstThumbnail;
    private String secondThumbnail;
    private String thirdThumbnail;
    private String fourthThumbnail;
    private Double popularity;
    private Boolean isDefault;

    @Enumerated(EnumType.STRING)
    @Column(name = "public_of_collection_status")
    @NotNull
    private PublicOfCollectionStatus publicOfCollectionStatus;

    @CreatedDate
    @Column(updatable = false)
    private LocalDateTime createdDate;

    private LocalDateTime lastModifiedDate;

    @OneToMany(mappedBy = "collection", cascade = CascadeType.ALL, fetch = FetchType.LAZY, orphanRemoval = true)
    private List<ViewedCollectionByIp> viewedCollectionByIps = new ArrayList<>();

    @OneToMany(mappedBy = "collection", cascade = CascadeType.ALL, fetch = FetchType.LAZY, orphanRemoval = true)
    private List<VisitedCollectionByMember> visitedCollectionByMembers = new ArrayList<>();

    @OneToMany(mappedBy = "collection", cascade = CascadeType.ALL, fetch = FetchType.LAZY, orphanRemoval = true)
    private List<LikedCollection> likedCollections = new ArrayList<>();

    @OneToMany(mappedBy = "collection", cascade = CascadeType.ALL, fetch = FetchType.LAZY, orphanRemoval = true)
    private List<FollowedCollection> followingMembers = new ArrayList<>();

    @OneToMany(mappedBy = "collection", fetch = FetchType.LAZY)
    private List<CollectedPost> collectedPosts = new ArrayList<>();

    @OneToMany(mappedBy = "collection", cascade = CascadeType.ALL, fetch = FetchType.LAZY, orphanRemoval = true)
    private List<CommentInCollection> commentInCollections = new ArrayList<>();

    //==연관관계 메소드==//

    private Long addViewedCollectionByIp(String ip) {
        ViewedCollectionByIp viewedCollectionByIp = ViewedCollectionByIp.createViewedCollectionByIp(ip, this);

        this.viewedCollectionByIps.add(viewedCollectionByIp);
        return viewedCollectionByIp.getId();
    }

    public void addVisitedCollectionByMember(Member member) {
        VisitedCollectionByMember visitedCollectionByMember = VisitedCollectionByMember.createVisitedCollectionByMember(member);

        this.visitedCollectionByMembers.add(visitedCollectionByMember);
        visitedCollectionByMember.updateCollections(this);
    }

    private Long addLikedCollectionByMember(Member member) {
        LikedCollection likedCollection = LikedCollection.createLikedCollection(member, this);

        this.likedCollections.add(likedCollection);

        return likedCollection.getId();
    }

    private Long addFollowingMemberByMember(Member member) {
        FollowedCollection followedCollection = FollowedCollection.createFollowedCollection(member, this);

        this.followingMembers.add(followedCollection);
        member.addFollowedCollection(followedCollection);

        return followedCollection.getId();
    }

    public void addCollectedPost(CollectedPost collectedPost) {
        updateThumbnail(collectedPost.getPost());
        this.collectedPosts.add(collectedPost);
        updateLastModifiedDate();
    }

    public void addCommentInCollection(CommentInCollection commentInCollection) {

        this.commentInCollections.add(commentInCollection);
        commentInCollection.updateCollection(this);
    }

    //==생성 메소드==//
    public static Collection createCollection(Member member, String title,
                                              String content, PublicOfCollectionStatus publicOfCollectionStatus)
            throws IncorrectTitleOfCollectionException, IncorrectContentOfCollectionException {

        Collection collection = new Collection();
        collection.updateMember(member);
        collection.updateTitle(title);
        collection.updateContent(content);
        collection.updatePublicOfCollectionStatus(publicOfCollectionStatus);
        collection.updatePopularityZero();
        collection.updateLastModifiedDate();
        collection.updateIsDefault(false);

        return collection;
    }

    public static Collection createDefaultCollection(Member member) {

        final String DEFAULT_COLLECTION_TITLE = "나중에 볼 컬렉션";
        final String DEFAULT_COLLECTION_CONTENT = "";
        final PublicOfCollectionStatus DEFAULT_COLLECTION_PUBLIC_STATUS = PublicOfCollectionStatus.B;

        Collection collection = new Collection();
        collection.updateMember(member);
        collection.updateTitle(DEFAULT_COLLECTION_TITLE);
        collection.updateContent(DEFAULT_COLLECTION_CONTENT);
        collection.updatePublicOfCollectionStatus(DEFAULT_COLLECTION_PUBLIC_STATUS);
        collection.updatePopularityZero();
        collection.updateLastModifiedDate();
        collection.updateIsDefault(true);

        return collection;

    }
    public static Collection createDocentCollection(Member member) {

        final String DOCENT_COLLECTION_TITLE = "도슨트 컬렉션";
        final String DOCENT_COLLECTION_CONTENT = "";
        final PublicOfCollectionStatus DOCENT_COLLECTION_PUBLIC_STATUS = PublicOfCollectionStatus.A;

        Collection collection = new Collection();
        collection.updateMember(member);
        collection.updateTitle(DOCENT_COLLECTION_TITLE);
        collection.updateContent(DOCENT_COLLECTION_CONTENT);
        collection.updatePublicOfCollectionStatus(DOCENT_COLLECTION_PUBLIC_STATUS);
        collection.updatePopularityZero();
        collection.updateLastModifiedDate();
        collection.updateIsDefault(true);

        return collection;
    }

    public static Collection createCollection(Long id) {
        Collection collection = new Collection();
        collection.updateId(id);

        return collection;
    }

    //==수정 메소드==//
    private void updateIsDefault(boolean isDefault) {
        this.isDefault = isDefault;
    }
    private void updateId(Long id) {
        this.id = id;
    }

    public void updateCollectedPosts(List<CollectedPost> collectedPosts) {
        this.collectedPosts = collectedPosts;
    }

    public void updatePopularityZero() {
        popularity = 0.0;
    }

    public void updateLastModifiedDate() {
        this.lastModifiedDate = LocalDateTime.now();
    }

    public void updatePopularity() {
        popularity = executeViewScore() + executeLikeScore() + executeCommentScore() + executeFollowScore();
    }
    public void updatePublicOfCollectionStatus(PublicOfCollectionStatus publicOfCollectionStatus) {
        this.publicOfCollectionStatus = publicOfCollectionStatus;
    }

    public void updateContent(String content) {
        isCorrectContent(content);
        this.content = content;
    }

    public void updateTitle(String title) {
        isCorrectTitle(title);
        this.title = title;
    }

    public void updateMember(Member member) {
        this.member = member;
    }

    public void updateFirstThumbnail(String firstThumbnail) {
        this.firstThumbnail = firstThumbnail;
    }
    public void updateSecondThumbnail(String secondThumbnail) {
        this.secondThumbnail = secondThumbnail;
    }
    public void updateThirdThumbnail(String thirdThumbnail) {
        this.thirdThumbnail = thirdThumbnail;
    }
    public void updateFourthThumbnail(String fourthThumbnail) {
        this.fourthThumbnail = fourthThumbnail;
    }

    public void updateThumbnail(Post post) {
        if (post.getThumbnailUrl() != null) {
            fourthThumbnail = thirdThumbnail;
            thirdThumbnail = secondThumbnail;
            secondThumbnail = firstThumbnail;
            firstThumbnail = post.getThumbnailUrl();
        }
    }

    //==비즈니스 로직==//

    private void isCorrectTitle(String title) {
        if (TITLE.isNotCorrect(title)) {
            throw new IncorrectTitleOfCollectionException(ErrorEnum.INCORRECT_TITLE_OF_COLLECTION);
        }
    }

    private void isCorrectContent(String content) {
        if (CONTENT.isNotCorrect(content)) {
            throw new IncorrectContentOfCollectionException(ErrorEnum.INCORRECT_CONTENT_OF_COLLECTION);
        }
    }
    public double executeViewScore() {

        LocalDateTime currentTime = LocalDateTime.now();
        ViewedCollectionByIp viewedCollectionByIp;
        int j = 0, i = viewedCollectionByIps.size() - 1;
        double result = 0, total = 0;

        while (i > -1) {
            viewedCollectionByIp = viewedCollectionByIps.get(i);
            if (isInTimeViewedCollect(currentTime, viewedCollectionByIp, j)) {
                result += 1;
                i--;
            } else {
                if (j == COLLECTION_VIEW.getScores().size() - 1) {
                    break;
                }
                total += COLLECTION_VIEW.getScores().get(j) * result;
                result = 0;
                j++;
            }
        }
        return (total + COLLECTION_VIEW.getScores().get(j) * result) * COLLECTION_VIEW.getRate();
    }

    public double executeLikeScore() {
        LocalDateTime currentTime = LocalDateTime.now();
        LikedCollection likedCollection;
        int j = 0, i = likedCollections.size() - 1;
        double result = 0, total = 0;

        while (i > -1) {
            likedCollection = likedCollections.get(i);
            if (isInTimeLikedCollect(currentTime, likedCollection, j)) {
                result += 1;
                i--;
            } else {
                if (j == COLLECTION_LIKE.getScores().size() - 1) {
                    break;
                }
                total += COLLECTION_LIKE.getScores().get(j) * result;
                result = 0;
                j++;
            }
        }
        return (total + COLLECTION_LIKE.getScores().get(j) * result) * COLLECTION_LIKE.getRate();
    }

    public double executeCommentScore() {

        LocalDateTime currentTime = LocalDateTime.now();
        CommentInCollection commentInCollection;
        int j = 0, i = commentInCollections.size() - 1;
        double result = 0, total = 0;

        while (i > -1) {
            commentInCollection = commentInCollections.get(i);
            if (isInTimeCommentInCollect(currentTime, commentInCollection, j)) {
                result += 1;
                i--;
            } else {
                if (j == COLLECTION_COMMENT.getScores().size() - 1) {
                    break;
                }
                total += COLLECTION_COMMENT.getScores().get(j) * result;
                result = 0;
                j++;
            }
        }
        return (total + COLLECTION_COMMENT.getScores().get(j) * result) * COLLECTION_COMMENT.getRate();
    }

    public double executeFollowScore() {
        LocalDateTime currentTime = LocalDateTime.now();
        FollowedCollection followedCollection;
        int j = 0, i = followingMembers.size() - 1;
        double result = 0, total = 0;

        while (i > -1) {
            followedCollection = followingMembers.get(i);
            if (isInTimeFollowedCollect(currentTime, followedCollection, j)) {
                result += 1;
                i--;
            } else {
                if (j == COLLECTION_FOLLOW.getScores().size() - 1) {
                    break;
                }
                total += COLLECTION_FOLLOW.getScores().get(j) * result;
                result = 0;
                j++;
            }
        }
        return (total + COLLECTION_FOLLOW.getScores().get(j) * result) * COLLECTION_FOLLOW.getRate();
    }

    public Long insertFollowedCollectionByMember(Member member) throws AlreadyFollowedCollectionException, CantFollowOwnCollectionException {
        if (this.member.equals(member)) {
            throw new CantFollowOwnCollectionException(ErrorEnum.CANT_FOLLOW_OWN_COLLECTION);
        }
        else if (isNotAlreadyMemberFollowCollection(member)) {
            return addFollowingMemberByMember(member);
        } else {
            throw new AlreadyFollowedCollectionException(ErrorEnum.ALREADY_FOLLOWED_COLLECTION);
        }
    }

    public Long insertViewedCollectionByIp(String ip) {

        if (isNotAlreadyIpView(ip)) {
            return addViewedCollectionByIp(ip);
        } else {
            return -1L;
        }
    }

    public Long insertLikedCollectionByMember(Member member) throws AlreadyLikedCollectionException {

        if (isNotAlreadyMemberLikeCollection(member)) {
            return addLikedCollectionByMember(member);
        } else {
            throw new AlreadyLikedCollectionException(ErrorEnum.ALREADY_LIKED_COLLECTION);
        }
    }

    public CommentInCollection getParentCommentById(Long commentInCollectionId)
            throws NotExistCommentInCollectionException {

        if (commentInCollectionId == null)
            return null;

        return commentInCollections.stream()
                .filter(comment -> comment.getId().equals(commentInCollectionId))
                .findAny().orElseThrow(() -> new NotExistCommentInCollectionException(ErrorEnum.NOT_EXIST_COMMENT_IN_COLLECTION));
    }
    //==조회 로직==//
    private boolean isInTimeViewedCollect(LocalDateTime currentTime, ViewedCollectionByIp viewedCollectionByIp, int j) {
        return ChronoUnit.DAYS
                .between(viewedCollectionByIp.getCreatedDate(), currentTime) >= COLLECTION_VIEW.getDays().get(j) &&
                ChronoUnit.DAYS
                        .between(viewedCollectionByIp.getCreatedDate(), currentTime) < COLLECTION_VIEW.getDays().get(j + 1);
    }

    private boolean isInTimeLikedCollect(LocalDateTime currentTime, LikedCollection likedCollection, int j) {
        return ChronoUnit.DAYS
                .between(likedCollection.getCreatedDate(), currentTime) >= COLLECTION_LIKE.getDays().get(j) &&
                ChronoUnit.DAYS
                        .between(likedCollection.getCreatedDate(), currentTime) < COLLECTION_LIKE.getDays().get(j + 1);
    }

    private boolean isInTimeCommentInCollect(LocalDateTime currentTime, CommentInCollection commentInCollection, int j) {
        return ChronoUnit.DAYS
                .between(commentInCollection.getCreatedDate(), currentTime) >= COLLECTION_COMMENT.getDays().get(j) &&
                ChronoUnit.DAYS
                        .between(commentInCollection.getCreatedDate(), currentTime) < COLLECTION_COMMENT.getDays().get(j + 1);
    }

    private boolean isInTimeFollowedCollect(LocalDateTime currentTime, FollowedCollection followedCollection, int j) {
        return ChronoUnit.DAYS
                .between(followedCollection.getCreatedDate(), currentTime) >= COLLECTION_FOLLOW.getDays().get(j) &&
                ChronoUnit.DAYS
                        .between(followedCollection.getCreatedDate(), currentTime) < COLLECTION_FOLLOW.getDays().get(j + 1);
    }
    public Integer getViewSize() {
        return viewedCollectionByIps.size();
    }

    public Boolean isNotAlreadyIpView(String ip) {

        LocalDateTime currentTime = LocalDateTime.now();

        List<ViewedCollectionByIp> views = viewedCollectionByIps.stream()
                .filter(viewedCollectionByIp -> viewedCollectionByIp.getIp().equals(ip))
                .collect(Collectors.toList());

        if (views.isEmpty()) {
            return true;
        }
        return ChronoUnit.MINUTES
                .between(views.get(views.size() - 1).getCreatedDate(), currentTime) >= VIEWED_BY_IP_MINUTE.getTime();

    }

    public Boolean isNotAlreadyMemberLikeCollection(Member member) {
        return this.likedCollections.stream().noneMatch(likedCollection -> likedCollection.getMember().equals(member));
    }

    public Boolean isNotAlreadyMemberFollowCollection(Member member) {
        return this.followingMembers.stream()
                .noneMatch(followedCollection -> followedCollection.getMember().equals(member));
    }
}
