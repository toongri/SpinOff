package com.nameless.spin_off.repository.query;

import com.nameless.spin_off.entity.collections.Collection;
import com.nameless.spin_off.entity.member.Member;
import com.nameless.spin_off.entity.movie.Movie;
import com.nameless.spin_off.entity.post.*;
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
import static com.nameless.spin_off.entity.collections.QCollectedPost.collectedPost;
import static com.nameless.spin_off.entity.collections.QCollection.collection;
import static com.nameless.spin_off.entity.member.QMember.member;
import static com.nameless.spin_off.entity.movie.QMovie.movie;
import static com.nameless.spin_off.entity.post.QPost.post;
import static com.nameless.spin_off.entity.post.QPostedHashtag.postedHashtag;

@Repository
@RequiredArgsConstructor
public class QuerydslMainPageQueryRepository implements MainPageQueryRepository {

    private final JPAQueryFactory jpaQueryFactory;

    @Override
    public Slice<Post> findPostsOrderByIdBySliced(Pageable pageable, Long memberId) {

        List<Post> content = jpaQueryFactory
                .select(post)
                .from(post)
                .join(post.member, member).fetchJoin()
                .where(post.publicOfPostStatus.in(DEFAULT_POST_PUBLIC),
                        memberIdNotEq(memberId))
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
    public Slice<Post> findPostsOrderByPopularityAfterLocalDateTimeSliced(
            Pageable pageable, LocalDateTime startDateTime, LocalDateTime endDateTime, Long memberId) {

        List<Post> content = jpaQueryFactory
                .select(post)
                .from(post)
                .join(post.member, member).fetchJoin()
                .where(post.createdDate.between(startDateTime, endDateTime),
                        post.publicOfPostStatus.in(DEFAULT_POST_PUBLIC),
                        memberIdNotEq(memberId))
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
            Pageable pageable, LocalDateTime startDateTime, LocalDateTime endDateTime, Long memberId) {

        long hourDuration = ChronoUnit.HOURS.between(startDateTime, endDateTime);

        List<Collection> collects = jpaQueryFactory
                .selectDistinct(collection)
                .from(collection)
                .join(collection.member, member).fetchJoin()
                .join(collection.collectedPosts, collectedPost).fetchJoin()
                .join(collectedPost.post, post).fetchJoin()
                .where(collection.collectedPosts.isNotEmpty(),
                        collection.publicOfCollectionStatus.in(DEFAULT_COLLECTION_PUBLIC),
                        memberIdNotEq(memberId))
                .orderBy(collection.popularity.desc(), collectedPost.createdDate.desc())
                .fetch();

        List<Collection> content = collects.stream()
                .filter(collect -> ChronoUnit.HOURS
                        .between(collect.getCollectedPosts().get(0).getCreatedDate(), endDateTime) < hourDuration)
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
    public Slice<Post> findPostsByFollowingMemberOrderByIdSliced(Pageable pageable, List<Member> followedMembers) {

        List<Post> content = jpaQueryFactory
                .select(post)
                .from(post)
                .join(post.member, member).fetchJoin()
                .where(member.in(followedMembers),
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
    public Slice<Collection> findCollectionsByFollowedMemberOrderByIdSliced(Pageable pageable, List<Member> followedMembers) {

        List<Collection> content = jpaQueryFactory
                .select(collection)
                .from(collection)
                .join(collection.member, member).fetchJoin()
                .where(member.in(followedMembers),
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
    public Slice<Post> findPostsByFollowedHashtagsOrderByIdSliced(Pageable pageable, List<Hashtag> followedHashtags) {

        List<Post> posts = jpaQueryFactory
                .select(post)
                .from(post)
                .join(post.member, member).fetchJoin()
                .join(post.postedHashtags, postedHashtag).fetchJoin()
                .where(postedHashtag.hashtag.in(followedHashtags),
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
    public Slice<Post> findPostsByFollowedMoviesOrderByIdSliced(Pageable pageable, List<Movie> followedMovies) {

        List<Post> posts = jpaQueryFactory
                .select(post)
                .from(post)
                .join(post.member, member).fetchJoin()
                .join(post.movie, movie).fetchJoin()
                .where(movie.in(followedMovies),
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
    public Slice<Collection> findCollectionsByFollowedCollectionsOrderByIdSliced(Pageable pageable, List<Collection> collections) {

        List<Collection> collects = jpaQueryFactory
                .selectDistinct(collection)
                .from(collection)
                .join(collection.member, member).fetchJoin()
                .join(collection.collectedPosts, collectedPost).fetchJoin()
                .join(collectedPost.post, post).fetchJoin()
                .where(collection.collectedPosts.isNotEmpty(),
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

    private BooleanExpression memberIdNotEq(Long memberId) {
        return memberId != null ? member.id.ne(memberId) : null;
    }
}
