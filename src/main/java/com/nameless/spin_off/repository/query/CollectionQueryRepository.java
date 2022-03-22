package com.nameless.spin_off.repository.query;

import com.nameless.spin_off.dto.CollectionDto.CollectionNameDto;
import com.nameless.spin_off.dto.CollectionDto.MainPageCollectionDto;
import com.nameless.spin_off.dto.CollectionDto.SearchAllCollectionDto;
import com.nameless.spin_off.dto.CollectionDto.SearchCollectionDto;
import com.nameless.spin_off.dto.QCollectionDto_CollectionNameDto;
import com.nameless.spin_off.dto.QCollectionDto_MainPageCollectionDto;
import com.nameless.spin_off.entity.collection.Collection;
import com.nameless.spin_off.entity.enums.member.BlockedMemberStatus;
import com.nameless.spin_off.entity.member.Member;
import com.nameless.spin_off.entity.member.QBlockedMember;
import com.nameless.spin_off.repository.support.Querydsl4RepositorySupport;
import com.querydsl.core.types.dsl.BooleanExpression;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Slice;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;

import static com.nameless.spin_off.entity.collection.QCollectedPost.collectedPost;
import static com.nameless.spin_off.entity.collection.QCollection.collection;
import static com.nameless.spin_off.entity.collection.QFollowedCollection.followedCollection;
import static com.nameless.spin_off.entity.collection.QLikedCollection.likedCollection;
import static com.nameless.spin_off.entity.collection.QViewedCollectionByIp.viewedCollectionByIp;
import static com.nameless.spin_off.entity.enums.collection.CollectionPublicEnum.DEFAULT_COLLECTION_PUBLIC;
import static com.nameless.spin_off.entity.enums.collection.CollectionPublicEnum.FOLLOW_COLLECTION_PUBLIC;
import static com.nameless.spin_off.entity.member.QBlockedMember.blockedMember;
import static com.nameless.spin_off.entity.member.QMember.member;

@Repository
public class CollectionQueryRepository extends Querydsl4RepositorySupport {

    public CollectionQueryRepository() {
        super(Collection.class);
    }

    public List<CollectionNameDto> findAllCollectionNamesByMemberIdOrderByCollectedPostDESC(Long memberId) {
        return getQueryFactory()
                .selectDistinct(new QCollectionDto_CollectionNameDto(collection.id, collection.title))
                .from(collection)
                .join(collection.collectedPosts, collectedPost)
                .where(collection.member.id.eq(memberId))
                .orderBy(collectedPost.id.desc())
                .fetch();
    }

    public Boolean isExist(Long id) {
        Integer fetchOne = getQueryFactory()
                .selectOne()
                .from(collection)
                .where(collection.id.eq(id))
                .fetchFirst();

        return fetchOne != null;
    }

    public Boolean isCorrectCollectionWithOwner(List<Long> collectionIds, Long memberId) {
        long length = getQueryFactory()
                .select(collection.count())
                .from(collection)
                .where(
                        collection.id.in(collectionIds),
                        collection.member.id.eq(memberId))
                .fetchCount();

        return collectionIds.size() == length;
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

    public Long getCollectionOwnerId(Long collectionId) {
        return getQueryFactory()
                .select(collection.member.id)
                .from(collection)
                .where(
                        collection.id.eq(collectionId))
                .fetchFirst();
    }

    public Slice<SearchAllCollectionDto> findAllSlicedForSearchPageAtAll(
            String keyword, Pageable pageable, List<Member> followedMembers, List<Member> blockedMembers) {
        Slice<Collection> content = applySlicing(pageable, contentQuery -> contentQuery
                .selectFrom(collection)
                .join(collection.member, member).fetchJoin()
                .where(
                        collection.title.contains(keyword),
                        memberNotIn(blockedMembers)));

        return MapContentToDtoForSearchPageAtAll(content, followedMembers);
    }

    public List<Collection> findAllByViewAfterTime(LocalDateTime time) {
        return getQueryFactory()
                .selectFrom(collection)
                .join(collection.viewedCollectionByIps, viewedCollectionByIp).fetchJoin()
                .where(
                        viewedCollectionByIp.createdDate.after(time))
                .fetch();
    }

    public Slice<SearchCollectionDto> findAllSlicedForSearchPageAtCollection(
            String keyword, Pageable pageable, List<Member> followedMembers, List<Member> blockedMembers) {
        Slice<Collection> content = applySlicing(pageable, contentQuery -> contentQuery
                .selectFrom(collection)
                .join(collection.member, member).fetchJoin()
                .where(
                        collection.title.contains(keyword),
                        memberNotIn(blockedMembers)));

        return MapContentToDtoForSearchPageAtCollection(content, followedMembers);
    }

    public Slice<MainPageCollectionDto> findAllSlicedForMainPage(
            Pageable pageable, Member user, List<Member> blockedMembers) {

        return applySlicing(pageable, contentQuery -> contentQuery
                .select(new QCollectionDto_MainPageCollectionDto(
                        collection.id, collection.title, member.id, member.nickname,
                        collection.firstThumbnail, collection.secondThumbnail))
                .from(collection)
                .join(collection.member, member)
                .where(collection.publicOfCollectionStatus.in(DEFAULT_COLLECTION_PUBLIC.getPrivacyBound()),
                        memberNotIn(blockedMembers),
                        memberNotEq(user)));
    }

    public Slice<MainPageCollectionDto> findAllByFollowedMemberSlicedForMainPage(
            Pageable pageable, List<Member> followedMembers, List<Member> blockedMembers) {

        return applySlicing(pageable, contentQuery -> contentQuery
                .select(new QCollectionDto_MainPageCollectionDto(
                        collection.id, collection.title, member.id, member.nickname,
                        collection.firstThumbnail, collection.secondThumbnail))
                .from(collection)
                .join(collection.member, member)
                .where(memberIn(followedMembers),
                        memberNotIn(blockedMembers),
                        collection.publicOfCollectionStatus.in(FOLLOW_COLLECTION_PUBLIC.getPrivacyBound())));
    }

    public Slice<MainPageCollectionDto> findAllByFollowedCollectionsSlicedForMainPage(
            Pageable pageable, List<Collection> followedCollections, List<Member> blockedMembers) {

        return applySlicing(pageable, contentQuery -> contentQuery
                .select(new QCollectionDto_MainPageCollectionDto(
                        collection.id, collection.title, member.id, member.nickname,
                        collection.firstThumbnail, collection.secondThumbnail))
                .from(collection)
                .join(collection.member, member)
                .where(memberNotIn(blockedMembers),
                        collectionIn(followedCollections)));
    }

    private Slice<SearchCollectionDto> MapContentToDtoForSearchPageAtCollection(
            Slice<Collection> contents, List<Member> followedMembers) {
        if (followedMembers.isEmpty()) {
            return contents.map(SearchCollectionDto::new);
        } else {
            return contents.map(content -> new SearchCollectionDto(content, followedMembers));
        }
    }

    private Slice<SearchAllCollectionDto> MapContentToDtoForSearchPageAtAll(
            Slice<Collection> contents, List<Member> followedMembers) {
        if (followedMembers.isEmpty()) {
            return contents.map(SearchAllCollectionDto::new);
        } else {
            return contents.map(content -> new SearchAllCollectionDto(content, followedMembers));
        }
    }
    private BooleanExpression memberNotEq(Member user) {
        return user != null ? member.ne(user) : null;
    }
    private BooleanExpression memberIn(List<Member> members) {
        return members.isEmpty() ? null : member.in(members);
    }
    private BooleanExpression collectionIn(List<Collection> collections) {
        return collections.isEmpty() ? null : collection.in(collections);
    }
    private BooleanExpression memberNotIn(List<Member> members) {
        return members.isEmpty() ? null : member.notIn(members);
    }
}
