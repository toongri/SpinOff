package com.nameless.spin_off.repository.query;

import com.nameless.spin_off.entity.collection.Collection;
import com.nameless.spin_off.entity.hashtag.Hashtag;
import com.nameless.spin_off.entity.member.Member;
import com.nameless.spin_off.entity.movie.Movie;
import com.nameless.spin_off.entity.post.Post;
import com.querydsl.core.types.dsl.BooleanExpression;
import com.querydsl.jpa.impl.JPAQueryFactory;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Slice;
import org.springframework.data.domain.SliceImpl;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.List;
import java.util.stream.Collectors;

import static com.nameless.spin_off.StaticVariable.*;
import static com.nameless.spin_off.entity.collection.QCollectedPost.collectedPost;
import static com.nameless.spin_off.entity.collection.QCollection.collection;
import static com.nameless.spin_off.entity.hashtag.QPostedHashtag.postedHashtag;
import static com.nameless.spin_off.entity.member.QMember.member;
import static com.nameless.spin_off.entity.movie.QMovie.movie;
import static com.nameless.spin_off.entity.post.QPost.post;

@Repository
@RequiredArgsConstructor
public class QuerydslMainPageQueryRepository implements MainPageQueryRepository {

    private final JPAQueryFactory jpaQueryFactory;

    @Override
    public Slice<Post> findPostsOrderByIdBySliced(Pageable pageable, Member user, List<Member> blockedMembers) {

        List<Post> content = jpaQueryFactory
                .select(post)
                .from(post)
                .join(post.member, member).fetchJoin()
                .where(post.publicOfPostStatus.in(DEFAULT_POST_PUBLIC),
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
    public Slice<Post> findPostsOrderByPopularityAfterLocalDateTimeSliced(Pageable pageable, Member user, List<Member> blockedMembers) {

        LocalDateTime now = LocalDateTime.now();
        LocalDateTime startTime = now.minusDays(POPULARITY_DATE_DURATION);

        List<Post> content = jpaQueryFactory
                .select(post)
                .from(post)
                .join(post.member, member).fetchJoin()
                .where(post.createdDate.between(startTime, now),
                        post.publicOfPostStatus.in(DEFAULT_POST_PUBLIC),
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
    public Slice<Collection> findCollectionsOrderByPopularityAfterLocalDateTimeSliced(
            Pageable pageable, Member user, List<Member> blockedMembers) {

        LocalDateTime now = LocalDateTime.now();

        List<Collection> collects = jpaQueryFactory
                .selectDistinct(collection)
                .from(collection)
                .join(collection.member, member).fetchJoin()
                .join(collection.collectedPosts, collectedPost).fetchJoin()
                .join(collectedPost.post, post).fetchJoin()
                .where(collection.collectedPosts.isNotEmpty(),
                        collection.publicOfCollectionStatus.in(DEFAULT_COLLECTION_PUBLIC),
                        member.notIn(blockedMembers),
                        memberNotEq(user))
                .orderBy(collection.popularity.desc(), collectedPost.createdDate.desc())
                .fetch();

        List<Collection> content = collects.stream()
                .filter(collect -> ChronoUnit.HOURS
                        .between(collect.getCollectedPosts().get(0).getCreatedDate(), now) < POPULARITY_DATE_DURATION)
                .skip(pageable.getOffset())
                .limit(pageable.getPageSize() + 1).collect(Collectors.toList());

        boolean hasNext = false;
        if (content.size() > pageable.getPageSize()) {
            content.remove(pageable.getPageSize());
            hasNext = true;
        }

        return new SliceImpl<>(content, pageable, hasNext);
    }

    @Override
    public Slice<Post> findPostsByFollowingMemberOrderByIdSliced(Pageable pageable, List<Member> followedMembers, List<Member> blockedMembers) {

        List<Post> content = jpaQueryFactory
                .select(post)
                .from(post)
                .join(post.member, member).fetchJoin()
                .where(member.in(followedMembers),
                        member.notIn(blockedMembers),
                        post.publicOfPostStatus.in(FOLLOW_POST_PUBLIC))
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
    public Slice<Collection> findCollectionsByFollowedMemberOrderByIdSliced(Pageable pageable, List<Member> followedMembers, List<Member> blockedMembers) {

        List<Collection> content = jpaQueryFactory
                .select(collection)
                .from(collection)
                .join(collection.member, member).fetchJoin()
                .where(member.in(followedMembers),
                        member.notIn(blockedMembers),
                        collection.publicOfCollectionStatus.in(FOLLOW_COLLECTION_PUBLIC))
                .offset(pageable.getOffset())
                .limit(pageable.getPageSize() + 1)
                .orderBy(collection.id.desc())
                .fetch();

        boolean hasNext = false;
        if (content.size() > pageable.getPageSize()) {
            content.remove(pageable.getPageSize());
            hasNext = true;
        }

        return new SliceImpl<>(content, pageable, hasNext);
    }

    @Override
    public Slice<Post> findPostsByFollowedHashtagsOrderByIdSliced(Pageable pageable, List<Hashtag> followedHashtags, List<Member> blockedMembers) {

        List<Post> posts = jpaQueryFactory
                .selectDistinct(post)
                .from(post)
                .join(post.member, member).fetchJoin()
                .join(post.postedHashtags, postedHashtag).fetchJoin()
                .where(postedHashtag.hashtag.in(followedHashtags),
                        member.notIn(blockedMembers),
                        post.publicOfPostStatus.in(DEFAULT_POST_PUBLIC))
                .orderBy(post.id.desc())
                .fetch();

        List<Post> content = posts.stream()
                .skip(pageable.getOffset())
                .limit(pageable.getPageSize() + 1).collect(Collectors.toList());

        boolean hasNext = false;
        if (content.size() > pageable.getPageSize()) {
            content.remove(pageable.getPageSize());
            hasNext = true;
        }

        return new SliceImpl<>(content, pageable, hasNext);
    }

    @Override
    public Slice<Post> findPostsByFollowedMoviesOrderByIdSliced(Pageable pageable, List<Movie> followedMovies, List<Member> blockedMembers) {

        List<Post> posts = jpaQueryFactory
                .select(post)
                .from(post)
                .join(post.member, member).fetchJoin()
                .join(post.movie, movie).fetchJoin()
                .where(movie.in(followedMovies),
                        member.notIn(blockedMembers),
                        post.publicOfPostStatus.in(DEFAULT_POST_PUBLIC))
                .orderBy(post.id.desc())
                .fetch();

        List<Post> content = posts.stream()
                .skip(pageable.getOffset())
                .limit(pageable.getPageSize() + 1).collect(Collectors.toList());

        boolean hasNext = false;
        if (content.size() > pageable.getPageSize()) {
            content.remove(pageable.getPageSize());
            hasNext = true;
        }

        return new SliceImpl<>(content, pageable, hasNext);
    }

    @Override
    public Slice<Collection> findCollectionsByFollowedCollectionsOrderByIdSliced(Pageable pageable, List<Collection> collections, List<Member> blockedMembers) {

        List<Collection> collects = jpaQueryFactory
                .selectDistinct(collection)
                .from(collection)
                .join(collection.member, member).fetchJoin()
                .join(collection.collectedPosts, collectedPost).fetchJoin()
                .join(collectedPost.post, post).fetchJoin()
                .where(collection.collectedPosts.isNotEmpty(),
                        member.notIn(blockedMembers),
                        collection.in(collections))
                .orderBy(collection.popularity.desc(), collectedPost.createdDate.desc())
                .fetch();

        List<Collection> content = collects.stream()
                .skip(pageable.getOffset())
                .limit(pageable.getPageSize() + 1).collect(Collectors.toList());

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
