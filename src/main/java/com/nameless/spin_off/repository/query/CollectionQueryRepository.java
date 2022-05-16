package com.nameless.spin_off.repository.query;

import com.nameless.spin_off.dto.CollectionDto.*;
import com.nameless.spin_off.dto.MemberDto.MembersByContentDto;
import com.nameless.spin_off.dto.*;
import com.nameless.spin_off.entity.collection.CollectedPost;
import com.nameless.spin_off.entity.collection.Collection;
import com.nameless.spin_off.entity.enums.collection.PublicOfCollectionStatus;
import com.nameless.spin_off.entity.enums.member.BlockedMemberStatus;
import com.nameless.spin_off.entity.member.QBlockedMember;
import com.nameless.spin_off.repository.support.Querydsl4RepositorySupport;
import com.querydsl.core.types.dsl.BooleanExpression;
import com.querydsl.jpa.impl.JPAUpdateClause;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Slice;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

import static com.nameless.spin_off.entity.collection.QCollectedPost.collectedPost;
import static com.nameless.spin_off.entity.collection.QCollection.collection;
import static com.nameless.spin_off.entity.collection.QFollowedCollection.followedCollection;
import static com.nameless.spin_off.entity.collection.QLikedCollection.likedCollection;
import static com.nameless.spin_off.entity.collection.QViewedCollectionByIp.viewedCollectionByIp;
import static com.nameless.spin_off.entity.comment.QCommentInCollection.commentInCollection;
import static com.nameless.spin_off.entity.enums.collection.CollectionPublicEnum.*;
import static com.nameless.spin_off.entity.member.QBlockedMember.blockedMember;
import static com.nameless.spin_off.entity.member.QFollowedMember.followedMember;
import static com.nameless.spin_off.entity.member.QMember.member;
import static com.nameless.spin_off.entity.post.QPost.post;

@Repository
public class CollectionQueryRepository extends Querydsl4RepositorySupport {

    public CollectionQueryRepository() {
        super(Collection.class);
    }

    public List<Long> findAllFollowedCollectionIdByMemberId(Long memberId) {
        return getQueryFactory()
                .select(followedCollection.collection.id)
                .from(followedCollection)
                .where(followedCollection.member.id.eq(memberId))
                .fetch();

    }

    public List<FollowCollectionDto> findAllFollowCollectionByMemberId(Long memberId, List<Long> blockedMemberIds) {
        return getQueryFactory()
                .select(new QCollectionDto_FollowCollectionDto(
                        collection.id, collection.title, member.accountId, collection.firstThumbnail,
                        collection.secondThumbnail, collection.thirdThumbnail, collection.fourthThumbnail))
                .from(followedCollection)
                .join(followedCollection.collection, collection)
                .join(collection.member, member)
                .where(
                        collectionMemberNotIn(blockedMemberIds),
                        followedCollection.member.id.eq(memberId),
                        collection.publicOfCollectionStatus.in(DEFAULT_COLLECTION_PUBLIC.getPrivacyBound()))
                .orderBy(followedCollection.id.desc())
                .fetch();
    }

    public List<MembersByContentDto> findAllFollowMemberByCollectionId(Long collectionId, List<Long> blockedMemberIds) {
        return getQueryFactory()
                .select(new QMemberDto_MembersByContentDto(
                        member.id, member.profileImg, member.nickname, member.accountId))
                .from(followedCollection)
                .join(followedCollection.member, member)
                .where(
                        memberNotIn(blockedMemberIds),
                        followedCollection.collection.id.eq(collectionId))
                .orderBy(followedCollection.id.desc())
                .fetch();
    }

    public List<MembersByContentDto> findAllLikeMemberByCollectionId(Long collectionId, List<Long> blockedMemberIds) {
        return getQueryFactory()
                .select(new QMemberDto_MembersByContentDto(
                        member.id, member.profileImg, member.nickname, member.accountId))
                .from(likedCollection)
                .join(likedCollection.member, member)
                .where(
                        memberNotIn(blockedMemberIds),
                        likedCollection.collection.id.eq(collectionId))
                .orderBy(likedCollection.id.desc())
                .fetch();
    }

    public Optional<ReadCollectionDto> findByIdForRead(Long collectionId, List<Long> blockedMemberIds) {
        return Optional.ofNullable(getQueryFactory()
                .select(new QCollectionDto_ReadCollectionDto(
                        collection.id, member.id, member.profileImg, member.nickname, member.accountId, collection.title,
                        collection.createdDate, collection.lastModifiedDate, collection.content,
                        collection.publicOfCollectionStatus, likedCollection.countDistinct(),
                        commentInCollection.countDistinct(), followedCollection.countDistinct(),
                        collectedPost.countDistinct()))
                .from(collection)
                .join(collection.member, member)
                .leftJoin(collection.likedCollections, likedCollection)
                .on(likedCollection.member.id.notIn(blockedMemberIds))
                .leftJoin(collection.commentInCollections, commentInCollection)
                .on(commentInCollection.member.id.notIn(blockedMemberIds))
                .leftJoin(collection.followingMembers, followedCollection)
                .on(followedCollection.member.id.notIn(blockedMemberIds))
                .leftJoin(collection.collectedPosts, collectedPost)
                .on(collectedPost.post.member.id.notIn(blockedMemberIds))
                .groupBy(collection)
                .where(collection.id.eq(collectionId))
                .fetchFirst());
    }

    public List<Long> findAllIdByPostId(Long postId) {
        return getQueryFactory()
                .select(collectedPost.collection.id)
                .from(collectedPost)
                .where(collectedPost.post.id.eq(postId))
                .fetch();
    }

    public List<CollectedPost> findAllIdByPostIdAndMemberId(Long postId, Long memberId) {
        return getQueryFactory()
                .select(collectedPost)
                .from(collectedPost)
                .join(collectedPost.collection, collection)
                .where(
                        collectedPost.post.id.eq(postId),
                        collection.member.id.eq(memberId))
                .fetch();
    }

    public List<PostThumbnailsCollectionDto> findPostThumbnailsByCollectionIds(List<Long> collectionIds) {
        return getQueryFactory()
                .select(new QCollectionDto_PostThumbnailsCollectionDto(
                        collectedPost.collection.id, post.thumbnailUrl))
                .from(collectedPost)
                .join(collectedPost.post, post)
                .where(
                        collectedPost.collection.id.in(collectionIds))
                .orderBy(collectedPost.id.desc())
                .fetch();
    }

    public Slice<MyPageCollectionDto> findAllByMemberIdSliced(Long memberId, Pageable pageable,
                                                              boolean isFollowing, boolean isAdmin) {
        return applySlicing(pageable, contentQuery -> contentQuery
                .select(new QCollectionDto_MyPageCollectionDto(
                        collection.id, collection.title, collection.firstThumbnail, collection.secondThumbnail,
                        collection.thirdThumbnail, collection.fourthThumbnail))
                .from(collection)
                .where(
                        collection.publicOfCollectionStatus.in(getPrivacyBound(isFollowing, isAdmin)),
                        collection.member.id.eq(memberId))
                .orderBy(collection.isDefault.desc()));
    }

    public Optional<PublicOfCollectionStatus> findPublicByCollectionId(Long collectionId) {
        return Optional.ofNullable(getQueryFactory()
                .select(collection.publicOfCollectionStatus)
                .from(collection)
                .where(collection.id.eq(collectionId))
                .fetchFirst());
    }

    public Optional<CollectionIdAndOwnerIdAndPublicCollectionDto> findPublicAndOwnerIdAndIdByCommentId(Long commentId) {
        return Optional.ofNullable(getQueryFactory()
                .select(new QCollectionDto_CollectionIdAndOwnerIdAndPublicCollectionDto(
                        collection.id, collection.member.id, collection.publicOfCollectionStatus))
                .from(commentInCollection)
                .join(commentInCollection.collection, collection)
                .where(commentInCollection.id.eq(commentId))
                .fetchFirst());
    }

    public Optional<CollectionIdAndPublicCollectionDto> findPublicAndIdByCommentId(Long commentId) {
        return Optional.ofNullable(getQueryFactory()
                .select(new QCollectionDto_CollectionIdAndPublicCollectionDto(
                        collection.id, collection.publicOfCollectionStatus))
                .from(commentInCollection)
                .join(commentInCollection.collection, collection)
                .where(commentInCollection.id.eq(commentId))
                .fetchFirst());
    }

    public Optional<OwnerIdAndPublicCollectionDto> findCollectionOwnerIdAndPublic(Long collectionId) {
        return Optional.ofNullable(getQueryFactory()
                .select(new QCollectionDto_OwnerIdAndPublicCollectionDto(
                        collection.member.id, collection.publicOfCollectionStatus))
                .from(collection)
                .where(
                        collection.id.eq(collectionId))
                .fetchFirst());
    }

    public List<PostInCollectionDto> findAllByMemberIdOrderByCollectedPostDESC(Long memberId) {
        return getQueryFactory()
                .select(new QCollectionDto_PostInCollectionDto(collection.id, collection.title,
                        collection.publicOfCollectionStatus, collection.firstThumbnail))
                .from(collection)
                .where(collection.member.id.eq(memberId))
                .orderBy(collection.lastModifiedDate.desc())
                .fetch();
    }

    public Optional<QuickPostInCollectionDto> findOneByMemberIdOrderByCollectedPostDESC(Long memberId) {
        return Optional.ofNullable(getQueryFactory()
                .select(new QCollectionDto_QuickPostInCollectionDto(collection.id, collection.title))
                .from(collection)
                .where(collection.member.id.eq(memberId))
                .orderBy(collection.lastModifiedDate.desc())
                .fetchFirst());
    }

    public Optional<QuickPostInCollectionDto> findOneByMemberIdOrderByCollectedPostDESC(Long memberId,
                                                                                        List<Long> collectionIds) {
        return Optional.ofNullable(getQueryFactory()
                .select(new QCollectionDto_QuickPostInCollectionDto(collection.id, collection.title))
                .from(collection)
                .where(
                        collection.member.id.eq(memberId),
                        collectionNotIn(collectionIds))
                .orderBy(collection.lastModifiedDate.desc())
                .fetchFirst());
    }

    public Boolean isExist(Long id) {
        Integer fetchOne = getQueryFactory()
                .selectOne()
                .from(collection)
                .where(collection.id.eq(id))
                .fetchFirst();
        return fetchOne != null;
    }

    public Boolean isExistLikedCollection(Long memberId, Long collectionId) {
        Integer fetchOne = getQueryFactory()
                .selectOne()
                .from(likedCollection)
                .where(
                        likedCollection.member.id.eq(memberId),
                        likedCollection.collection.id.eq(collectionId))
                .fetchFirst();

        return fetchOne != null;
    }

    public Boolean isBlockMembersCollection(Long memberId, Long collectionId) {
        QBlockedMember blockingMember = new QBlockedMember("blockingMember");

        Integer fetchOne = getQueryFactory()
                .selectOne()
                .from(collection)
                .join(collection.member, member)
                .leftJoin(member.blockedMembers, blockedMember)
                .leftJoin(member.blockingMembers, blockingMember)
                .where(
                        collection.id.eq(collectionId).and(
                                ((blockedMember.member.id.eq(memberId).and(
                                        blockedMember.blockedMemberStatus.eq(BlockedMemberStatus.A))).or(
                                        blockingMember.blockingMember.id.eq(memberId).and(
                                                blockingMember.blockedMemberStatus.eq(BlockedMemberStatus.A))))))
                .fetchFirst();

        return fetchOne != null;
    }

    public Boolean isFollowMembersOrOwnerCollection(Long memberId, Long collectionId) {
        Integer fetchOne = getQueryFactory()
                .selectOne()
                .from(collection)
                .join(collection.member, member)
                .leftJoin(member.followingMembers, followedMember)
                .where(
                        collection.id.eq(collectionId).and(
                                followedMember.followingMember.id.eq(memberId).or(
                                        member.id.eq(memberId))))
                .fetchFirst();

        return fetchOne != null;
    }

    public Optional<Long> findOwnerIdByCollectionId(Long collectionId) {
        return Optional.ofNullable(getQueryFactory()
                .select(collection.member.id)
                .from(collection)
                .where(
                        collection.id.eq(collectionId))
                .fetchFirst());
    }

    public Boolean isExistFollowedCollection(Long memberId, Long collectionId) {
        Integer fetchOne = getQueryFactory()
                .selectOne()
                .from(followedCollection)
                .where(
                        followedCollection.member.id.eq(memberId),
                        followedCollection.collection.id.eq(collectionId))
                .fetchFirst();

        return fetchOne != null;
    }

    public Boolean isExistIp(Long id, String ip, LocalDateTime time) {
        Integer fetchOne = getQueryFactory()
                .selectOne()
                .from(viewedCollectionByIp)
                .where(
                        viewedCollectionByIp.ip.eq(ip),
                        viewedCollectionByIp.collection.id.eq(id),
                        viewedCollectionByIp.createdDate.after(time))
                .fetchFirst();

        return fetchOne != null;
    }

    public Slice<SearchAllCollectionDto> findAllSlicedForSearchPageAtAll(
            String keyword, Pageable pageable, List<Long> blockedMembers, Long memberId) {

        Slice<SearchAllCollectionDto> content = applySlicing(pageable, contentQuery -> contentQuery
                .select(new QCollectionDto_SearchAllCollectionDto(
                        collection.id, collection.title, member.id, member.accountId, collection.firstThumbnail,
                        followedCollection.count()))
                .from(collection)
                .join(collection.member, member)
                .leftJoin(collection.followingMembers, followedCollection)
                .on(followedCollection.member.id.notIn(blockedMembers))
                .groupBy(collection)
                .where(
                        collection.publicOfCollectionStatus.in(DEFAULT_COLLECTION_PUBLIC.getPrivacyBound()),
                        collection.title.contains(keyword),
                        memberNotIn(blockedMembers)));

        if (memberId != null) {
            Map<Long, List<FollowCollectionMemberDto>> followingMembers =
                    getFollowingMembersAtCollection(memberId, getCollectionIdsAll(content.getContent()));

            content.getContent().forEach(o -> o.setFollowingMember(followingMembers.get(o.getCollectionId())));
        }

        return content;
    }

    public Slice<SearchCollectionDto> findAllSlicedForSearchPageAtCollection(
            String keyword, Pageable pageable, List<Long> blockedMembers, Long memberId) {

        Slice<SearchCollectionDto> content = applySlicing(pageable, contentQuery -> contentQuery
                .select(new QCollectionDto_SearchCollectionDto(
                        collection.id, collection.title, member.id, member.accountId,
                        collection.firstThumbnail, collection.secondThumbnail, collection.thirdThumbnail,
                        collection.fourthThumbnail, followedCollection.count()))
                .from(collection)
                .join(collection.member, member)
                .leftJoin(collection.followingMembers, followedCollection)
                .on(followedCollection.member.id.notIn(blockedMembers))
                .groupBy(collection)
                .where(
                        collection.publicOfCollectionStatus.in(DEFAULT_COLLECTION_PUBLIC.getPrivacyBound()),
                        collection.title.contains(keyword),
                        memberNotIn(blockedMembers)));

        if (memberId != null) {
            Map<Long, List<FollowCollectionMemberDto>> followingMembers =
                    getFollowingMembersAtCollection(memberId, getCollectionIds(content.getContent()));

            content.getContent().forEach(o -> o.setFollowingMember(followingMembers.get(o.getCollectionId())));
        }

        return content;
    }

    public List<Collection> findAllByViewAfterTime(LocalDateTime time) {
        return getQueryFactory()
                .selectFrom(collection)
                .join(collection.viewedCollectionByIps, viewedCollectionByIp).fetchJoin()
                .where(
                        viewedCollectionByIp.createdDate.after(time))
                .fetch();
    }

    public Slice<MainPageCollectionDto> findAllSlicedForMainPage(
            Pageable pageable, Long memberId, List<Long> blockedMemberIds) {
        List<Long> banList = new ArrayList<>(blockedMemberIds);
        addBanList(memberId, banList);

        return applySlicing(pageable, contentQuery -> contentQuery
                .select(new QCollectionDto_MainPageCollectionDto(
                        collection.id, collection.title, member.id, member.nickname,
                        collection.firstThumbnail, collection.secondThumbnail))
                .from(collection)
                .join(collection.member, member)
                .where(
                        collection.publicOfCollectionStatus.in(DEFAULT_COLLECTION_PUBLIC.getPrivacyBound()),
                        memberNotIn(banList)));
    }

    public Slice<MainPageCollectionDto> findAllByFollowedMemberSlicedForMainPage(
            Pageable pageable, Long memberId) {

        return applySlicing(pageable, contentQuery -> contentQuery
                .select(new QCollectionDto_MainPageCollectionDto(
                        collection.id, collection.title, member.id, member.nickname,
                        collection.firstThumbnail, collection.secondThumbnail))
                .from(collection)
                .join(collection.member, member)
                .join(member.followingMembers, followedMember)
                .where(
                        followedMember.followingMember.id.eq(memberId),
                        collection.publicOfCollectionStatus.in(FOLLOW_COLLECTION_PUBLIC.getPrivacyBound())));
    }

    public Slice<MainPageCollectionDto> findAllByFollowedCollectionsSlicedForMainPage(
            Pageable pageable, Long memberId) {

        return applySlicing(pageable, contentQuery -> contentQuery
                .select(new QCollectionDto_MainPageCollectionDto(
                        collection.id, collection.title, member.id, member.nickname,
                        collection.firstThumbnail, collection.secondThumbnail))
                .from(collection)
                .join(collection.followingMembers, followedCollection)
                .join(collection.member, member)
                .where(
                        followedCollection.member.id.eq(memberId)));
    }

    private Map<Long, List<FollowCollectionMemberDto>> getFollowingMembersAtCollection(Long memberId, List<Long> collectionIds) {
        return getQueryFactory()
                .select(new QCollectionDto_FollowCollectionMemberDto(
                        followedCollection.collection.id, member.id, member.popularity, member.nickname))
                .from(followedCollection)
                .join(followedCollection.member, member)
                .join(member.followingMembers, followedMember)
                .where(
                        followedCollection.collection.id.in(collectionIds),
                        followedMember.followingMember.id.eq(memberId))
                .fetch().stream().collect(Collectors.groupingBy(FollowCollectionMemberDto::getCollectionId));
    }

    public long updateCollectionsThumbnails(String thumbnail, List<Long> collectionIds) {
        if (thumbnail == null) {
            return getQueryFactory()
                    .update(collection)
                    .set(collection.lastModifiedDate, LocalDateTime.now())
                    .where(collection.id.in(collectionIds))
                    .execute();
        } else {
            return getQueryFactory()
                    .update(collection)
                    .set(collection.lastModifiedDate, LocalDateTime.now())
                    .set(collection.fourthThumbnail, collection.thirdThumbnail)
                    .set(collection.thirdThumbnail, collection.secondThumbnail)
                    .set(collection.secondThumbnail, collection.firstThumbnail)
                    .set(collection.firstThumbnail, thumbnail)
                    .where(collection.id.in(collectionIds))
                    .execute();
        }
    }

    public long resetCollectionThumbnail(Long collectionId, List<String> thumbnails) {
        int size = thumbnails.size();
        JPAUpdateClause updateClause = getQueryFactory()
                .update(collection);
        if (size >= 4) {
            updateClause = updateClause
                    .set(collection.fourthThumbnail, thumbnails.get(3))
                    .set(collection.thirdThumbnail, thumbnails.get(2))
                    .set(collection.secondThumbnail, thumbnails.get(1))
                    .set(collection.firstThumbnail, thumbnails.get(0));
        } else if (size == 3) {
            updateClause = updateClause
                    .setNull(collection.fourthThumbnail)
                    .set(collection.thirdThumbnail, thumbnails.get(2))
                    .set(collection.secondThumbnail, thumbnails.get(1))
                    .set(collection.firstThumbnail, thumbnails.get(0));
        } else if (size == 2) {
            updateClause = updateClause
                    .setNull(collection.fourthThumbnail)
                    .setNull(collection.thirdThumbnail)
                    .set(collection.secondThumbnail, thumbnails.get(1))
                    .set(collection.firstThumbnail, thumbnails.get(0));
        } else if (size == 1) {
            updateClause = updateClause
                    .setNull(collection.fourthThumbnail)
                    .setNull(collection.thirdThumbnail)
                    .setNull(collection.secondThumbnail)
                    .set(collection.firstThumbnail, thumbnails.get(0));
        } else {
            updateClause = updateClause
                    .setNull(collection.fourthThumbnail)
                    .setNull(collection.thirdThumbnail)
                    .setNull(collection.secondThumbnail)
                    .setNull(collection.firstThumbnail);
        }
        return updateClause
                .where(collection.id.eq(collectionId))
                .execute();
    }

    private void addBanList(Long memberId, List<Long> blockedMemberIds) {
        if (memberId != null) {
            blockedMemberIds.add(memberId);
        }
    }

    private List<PublicOfCollectionStatus> getPrivacyBound(boolean isFollowing, boolean isAdmin) {
        if (isAdmin) {
            return ADMIN_COLLECTION_PUBLIC.getPrivacyBound();
        } else if (isFollowing) {
            return FOLLOW_COLLECTION_PUBLIC.getPrivacyBound();
        } else {
            return DEFAULT_COLLECTION_PUBLIC.getPrivacyBound();
        }
    }

    private BooleanExpression followingCollectionNotIn(List<Long> members) {
        return members.isEmpty() ? null : followedCollection.member.id.notIn(members).or(followedCollection.isNull());
    }
    private BooleanExpression memberNotIn(List<Long> memberIds) {
        return memberIds.isEmpty() ? null : member.id.notIn(memberIds);
    }

    private BooleanExpression collectionMemberNotIn(List<Long> memberIds) {
        return memberIds.isEmpty() ? null : collection.member.id.notIn(memberIds);
    }

    private BooleanExpression collectionNotIn(List<Long> collectionIds) {
        return collectionIds.isEmpty() ? null : collection.id.notIn(collectionIds);
    }

    private List<Long> getCollectionIds(List<SearchCollectionDto> content) {
        return content.stream().map(SearchCollectionDto::getCollectionId).collect(Collectors.toList());
    }

    private List<Long> getCollectionIdsAll(List<SearchAllCollectionDto> content) {
        return content.stream().map(SearchAllCollectionDto::getCollectionId).collect(Collectors.toList());
    }
}
