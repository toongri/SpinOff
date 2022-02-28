package com.nameless.spin_off.repository.query;

import com.nameless.spin_off.dto.CollectionDto.MainPageCollectionDto;
import com.nameless.spin_off.dto.CollectionDto.SearchPageAtAllCollectionDto;
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

    public Slice<SearchPageAtAllCollectionDto> findAllSlicedSearchPageAtAll(String keyword, Pageable pageable,
                                                                            List<Member> followedMembers) {
        Slice<Collection> content = applySlicing(pageable, contentQuery -> contentQuery
                .selectFrom(collection)
                .join(collection.member, member).fetchJoin()
                .where(collection.title.contains(keyword)));

        return MapContentToDtoForSearchPage(content, followedMembers);
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
                        member.notIn(blockedMembers),
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
                .where(member.in(followedMembers),
                        member.notIn(blockedMembers),
                        collection.publicOfCollectionStatus.in(FOLLOW_COLLECTION_PUBLIC.getPrivacyBound())));
    }

    public Slice<MainPageCollectionDto> findAllByFollowedCollectionsSlicedForMainPage(
            Pageable pageable, List<Collection> collections, List<Member> blockedMembers) {

        return applySlicing(pageable, contentQuery -> contentQuery
                .select(new QCollectionDto_MainPageCollectionDto(
                        collection.id, collection.title, member.id, member.nickname,
                        collection.firstThumbnail, collection.secondThumbnail))
                .from(collection)
                .join(collection.member, member)
                .where(member.notIn(blockedMembers),
                        collection.in(collections)));
    }

    private Slice<SearchPageAtAllCollectionDto> MapContentToDtoForSearchPage(
            Slice<Collection> contents, List<Member> followedMembers) {
        if (followedMembers.isEmpty()) {
            return contents.map(SearchPageAtAllCollectionDto::new);
        } else {
            return contents.map(content -> new SearchPageAtAllCollectionDto(content, followedMembers));
        }
    }
    private BooleanExpression memberNotEq(Member user) {
        return user != null ? member.ne(user) : null;
    }
    private BooleanExpression memberIn(List<Member> members) {
        return members.isEmpty() ? null : member.in(members);
    }
    private BooleanExpression memberNotIn(List<Member> members) {
        return members.isEmpty() ? null : member.notIn(members);
    }
}
