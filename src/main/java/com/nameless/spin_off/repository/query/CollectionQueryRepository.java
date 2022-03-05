package com.nameless.spin_off.repository.query;

import com.nameless.spin_off.dto.CollectionDto.MainPageCollectionDto;
import com.nameless.spin_off.dto.CollectionDto.SearchAllCollectionDto;
import com.nameless.spin_off.dto.CollectionDto.SearchCollectionDto;
import com.nameless.spin_off.dto.QCollectionDto_MainPageCollectionDto;
import com.nameless.spin_off.entity.collection.Collection;
import com.nameless.spin_off.entity.member.Member;
import com.nameless.spin_off.repository.support.Querydsl4RepositorySupport;
import com.querydsl.core.types.dsl.BooleanExpression;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Slice;
import org.springframework.stereotype.Repository;

import java.util.List;

import static com.nameless.spin_off.entity.collection.QCollection.collection;
import static com.nameless.spin_off.entity.enums.collection.CollectionPublicEnum.DEFAULT_COLLECTION_PUBLIC;
import static com.nameless.spin_off.entity.enums.collection.CollectionPublicEnum.FOLLOW_COLLECTION_PUBLIC;
import static com.nameless.spin_off.entity.member.QMember.member;

@Repository
public class CollectionQueryRepository extends Querydsl4RepositorySupport {

    public CollectionQueryRepository() {
        super(Collection.class);
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
