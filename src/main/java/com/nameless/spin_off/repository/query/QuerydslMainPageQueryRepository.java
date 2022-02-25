package com.nameless.spin_off.repository.query;

import com.nameless.spin_off.dto.CollectionDto.MainPageCollectionDto;
import com.nameless.spin_off.dto.PostDto.MainPagePostDto;
import com.nameless.spin_off.dto.QCollectionDto_MainPageCollectionDto;
import com.nameless.spin_off.dto.QPostDto_MainPagePostDto;
import com.nameless.spin_off.entity.collection.Collection;
import com.nameless.spin_off.entity.hashtag.Hashtag;
import com.nameless.spin_off.entity.member.Member;
import com.nameless.spin_off.entity.movie.Movie;
import com.querydsl.core.types.dsl.BooleanExpression;
import com.querydsl.jpa.impl.JPAQueryFactory;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Slice;
import org.springframework.data.domain.SliceImpl;
import org.springframework.stereotype.Repository;

import java.util.List;

import static com.nameless.spin_off.entity.collection.QCollectedPost.collectedPost;
import static com.nameless.spin_off.entity.collection.QCollection.collection;
import static com.nameless.spin_off.entity.enums.collection.CollectionPublicEnum.DEFAULT_COLLECTION_PUBLIC;
import static com.nameless.spin_off.entity.enums.collection.CollectionPublicEnum.FOLLOW_COLLECTION_PUBLIC;
import static com.nameless.spin_off.entity.enums.post.PostPublicEnum.DEFAULT_POST_PUBLIC;
import static com.nameless.spin_off.entity.enums.post.PostPublicEnum.FOLLOW_POST_PUBLIC;
import static com.nameless.spin_off.entity.hashtag.QPostedHashtag.postedHashtag;
import static com.nameless.spin_off.entity.member.QMember.member;
import static com.nameless.spin_off.entity.movie.QMovie.movie;
import static com.nameless.spin_off.entity.post.QPost.post;

@Repository
@RequiredArgsConstructor
public class QuerydslMainPageQueryRepository implements MainPageQueryRepository {

    private final JPAQueryFactory jpaQueryFactory;

    @Override
    public Slice<MainPagePostDto> findPostsOrderByIdBySliced(Pageable pageable, Member user, List<Member> blockedMembers) {

        List<MainPagePostDto> content = jpaQueryFactory
                .select(new QPostDto_MainPagePostDto(
                        post.id, post.title, member.id, member.nickname, member.profileImg, post.thumbnailUrl))
                .from(post)
                .join(post.member, member).fetchJoin()
                .where(post.publicOfPostStatus.in(DEFAULT_POST_PUBLIC.getPrivacyBound()),
                        member.notIn(blockedMembers),
                        memberNotEq(user))
                .offset(pageable.getOffset())
                .limit(pageable.getPageSize() + 1)
                .orderBy(post.id.desc())
                .fetch();

        boolean hasNext = false;
        if (content.size() > pageable.getPageSize()) {
            content.remove(pageable.getPageSize());
            hasNext = true;
        }

        return new SliceImpl<>(content, pageable, hasNext);

//        JPAQuery<Post> countQuery = jpaQueryFactory
//                .selectFrom(post)
//                .where(post.createdDate.after(localDateTime));

//        return PageableExecutionUtils.getPage(posts, pageable, () -> countQuery.fetch().size());
    }

    @Override
    public Slice<MainPagePostDto> findPostsOrderByPopularitySliced(
            Pageable pageable, Member user, List<Member> blockedMembers) {

        List<MainPagePostDto> content = jpaQueryFactory
                .select(new QPostDto_MainPagePostDto(
                        post.id, post.title, member.id, member.nickname, member.profileImg, post.thumbnailUrl))
                .from(post)
                .join(post.member, member).fetchJoin()
                .where(post.publicOfPostStatus.in(DEFAULT_POST_PUBLIC.getPrivacyBound()),
                        member.notIn(blockedMembers),
                        memberNotEq(user))
                .offset(pageable.getOffset())
                .limit(pageable.getPageSize() + 1)
                .orderBy(post.popularity.desc())
                .fetch();

        boolean hasNext = false;
        if (content.size() > pageable.getPageSize()) {
            content.remove(pageable.getPageSize());
            hasNext = true;
        }

        return new SliceImpl<>(content, pageable, hasNext);
    }

    @Override
    public Slice<MainPageCollectionDto> findCollectionsOrderByPopularitySliced(
            Pageable pageable, Member user, List<Member> blockedMembers) {

        List<MainPageCollectionDto> content = jpaQueryFactory
                .select(new QCollectionDto_MainPageCollectionDto(
                        collection.id, collection.title, member.id, member.nickname,
                        collection.firstThumbnail, collection.secondThumbnail))
                .from(collection)
                .join(collection.member, member)
                .where(collection.publicOfCollectionStatus.in(DEFAULT_COLLECTION_PUBLIC.getPrivacyBound()),
                        member.notIn(blockedMembers),
                        memberNotEq(user))
                .orderBy(collection.popularity.desc())
                .offset(pageable.getOffset())
                .limit(pageable.getPageSize() + 1)
                .fetch();

        boolean hasNext = false;
        if (content.size() > pageable.getPageSize()) {
            content.remove(pageable.getPageSize());
            hasNext = true;
        }

        return new SliceImpl<>(content, pageable, hasNext);
    }

    @Override
    public Slice<MainPagePostDto> findPostsByFollowingMemberOrderByIdSliced(Pageable pageable, List<Member> followedMembers, List<Member> blockedMembers) {

        List<MainPagePostDto> content = jpaQueryFactory
                .select(new QPostDto_MainPagePostDto(
                        post.id, post.title, member.id, member.nickname, member.profileImg, post.thumbnailUrl))
                .from(post)
                .join(post.member, member)
                .where(member.in(followedMembers),
                        member.notIn(blockedMembers),
                        post.publicOfPostStatus.in(FOLLOW_POST_PUBLIC.getPrivacyBound()))
                .offset(pageable.getOffset())
                .limit(pageable.getPageSize() + 1)
                .orderBy(post.id.desc())
                .fetch();

        boolean hasNext = false;
        if (content.size() > pageable.getPageSize()) {
            content.remove(pageable.getPageSize());
            hasNext = true;
        }

        return new SliceImpl<>(content, pageable, hasNext);
    }

    @Override
    public Slice<MainPageCollectionDto> findCollectionsByFollowedMemberOrderByIdSliced(Pageable pageable, List<Member> followedMembers, List<Member> blockedMembers) {

        List<MainPageCollectionDto> content = jpaQueryFactory
                .select(new QCollectionDto_MainPageCollectionDto(
                        collection.id, collection.title, member.id, member.nickname,
                        collection.firstThumbnail, collection.secondThumbnail))
                .from(collection)
                .join(collection.member, member)
                .where(member.in(followedMembers),
                        member.notIn(blockedMembers),
                        collection.publicOfCollectionStatus.in(FOLLOW_COLLECTION_PUBLIC.getPrivacyBound()))
                .orderBy(collection.id.desc())
                .offset(pageable.getOffset())
                .limit(pageable.getPageSize() + 1)
                .fetch();

        boolean hasNext = false;
        if (content.size() > pageable.getPageSize()) {
            content.remove(pageable.getPageSize());
            hasNext = true;
        }

        return new SliceImpl<>(content, pageable, hasNext);
    }

    @Override
    public Slice<MainPagePostDto> findPostsByFollowedHashtagsOrderByIdSliced(Pageable pageable, List<Hashtag> followedHashtags, List<Member> blockedMembers) {

        List<MainPagePostDto> content = jpaQueryFactory
                .select(new QPostDto_MainPagePostDto(
                        post.id, post.title, member.id, member.nickname, member.profileImg, post.thumbnailUrl))
                .from(post)
                .join(post.member, member)
                .join(post.postedHashtags, postedHashtag)
                .where(postedHashtag.hashtag.in(followedHashtags),
                        member.notIn(blockedMembers),
                        post.publicOfPostStatus.in(DEFAULT_POST_PUBLIC.getPrivacyBound()))
                .offset(pageable.getOffset())
                .limit(pageable.getPageSize() + 1)
                .orderBy(post.id.desc())
                .fetch();
//                .stream()
//                .skip(pageable.getOffset())
//                .limit(pageable.getPageSize() + 1).collect(Collectors.toList());

        boolean hasNext = false;
        if (content.size() > pageable.getPageSize()) {
            content.remove(pageable.getPageSize());
            hasNext = true;
        }

        return new SliceImpl<>(content, pageable, hasNext);
    }

    @Override
    public Slice<MainPagePostDto> findPostsByFollowedMoviesOrderByIdSliced(Pageable pageable, List<Movie> followedMovies, List<Member> blockedMembers) {

        List<MainPagePostDto> content = jpaQueryFactory
                .select(new QPostDto_MainPagePostDto(
                        post.id, post.title, member.id, member.nickname, member.profileImg, post.thumbnailUrl))
                .from(post)
                .join(post.member, member)
                .join(post.movie, movie)
                .where(movie.in(followedMovies),
                        member.notIn(blockedMembers),
                        post.publicOfPostStatus.in(DEFAULT_POST_PUBLIC.getPrivacyBound()))
                .offset(pageable.getOffset())
                .limit(pageable.getPageSize() + 1)
                .orderBy(post.id.desc())
                .fetch();
//                .stream()
//                .skip(pageable.getOffset())
//                .limit(pageable.getPageSize() + 1).collect(Collectors.toList());

        boolean hasNext = false;
        if (content.size() > pageable.getPageSize()) {
            content.remove(pageable.getPageSize());
            hasNext = true;
        }

        return new SliceImpl<>(content, pageable, hasNext);
    }

    @Override
    public Slice<MainPageCollectionDto> findCollectionsByFollowedCollectionsOrderByIdSliced(Pageable pageable, List<Collection> collections, List<Member> blockedMembers) {

        List<MainPageCollectionDto> content = jpaQueryFactory
                .select(new QCollectionDto_MainPageCollectionDto(
                        collection.id, collection.title, member.id, member.nickname,
                        collection.firstThumbnail, collection.secondThumbnail))
                .from(collection)
                .join(collection.member, member)
                .join(collection.collectedPosts, collectedPost)
                .where(member.notIn(blockedMembers),
                        collection.in(collections))
                .orderBy(collectedPost.id.desc())
                .offset(pageable.getOffset())
                .limit(pageable.getPageSize() + 1)
                .fetch();

        boolean hasNext = false;
        if (content.size() > pageable.getPageSize()) {
            content.remove(pageable.getPageSize());
            hasNext = true;
        }

        return new SliceImpl<>(content, pageable, hasNext);
    }

    private BooleanExpression memberNotEq(Member user) {
        return user != null ? member.ne(user) : null;
    }
}
