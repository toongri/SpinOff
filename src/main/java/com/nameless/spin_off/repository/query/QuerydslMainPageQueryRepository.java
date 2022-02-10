package com.nameless.spin_off.repository.query;

import com.nameless.spin_off.entity.collections.Collection;
import com.nameless.spin_off.entity.collections.QCollectedPost;
import com.nameless.spin_off.entity.collections.QCollection;
import com.nameless.spin_off.entity.movie.Movie;
import com.nameless.spin_off.entity.post.Hashtag;
import com.nameless.spin_off.entity.post.Post;
import com.nameless.spin_off.entity.post.QHashtag;
import com.nameless.spin_off.entity.post.QPostedHashtag;
import com.querydsl.jpa.impl.JPAQueryFactory;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Slice;
import org.springframework.data.domain.SliceImpl;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import static com.nameless.spin_off.entity.collections.QCollectedPost.collectedPost;
import static com.nameless.spin_off.entity.collections.QCollection.collection;
import static com.nameless.spin_off.entity.member.QMember.member;
import static com.nameless.spin_off.entity.post.QHashtag.hashtag;
import static com.nameless.spin_off.entity.post.QPost.post;
import static com.nameless.spin_off.entity.post.QPostedHashtag.postedHashtag;

@Repository
@RequiredArgsConstructor
public class QuerydslMainPageQueryRepository implements MainPageQueryRepository {

    private final JPAQueryFactory jpaQueryFactory;

    @Override
    public Slice<Post> findPostsOrderByIdBySliced(Pageable pageable) {

        List<Post> content = jpaQueryFactory
                .select(post)
                .from(post)
                .join(post.member, member).fetchJoin()
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
            Pageable pageable, LocalDateTime startDateTime, LocalDateTime endDateTime) {

        List<Post> content = jpaQueryFactory
                .select(post)
                .from(post)
                .join(post.member, member).fetchJoin()
                .where(post.createdDate.between(startDateTime, endDateTime))
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
            Pageable pageable, LocalDateTime startDateTime, LocalDateTime endDateTime) {

        long hourDuration = ChronoUnit.HOURS.between(startDateTime, endDateTime);

        List<Collection> collects = jpaQueryFactory
                .selectDistinct(collection)
                .from(collection)
                .join(collection.member, member).fetchJoin()
                .join(collection.collectedPosts, collectedPost).fetchJoin()
                .join(collectedPost.post, post).fetchJoin()
                .where(collection.collectedPosts.isNotEmpty())
                .orderBy(collection.popularity.desc(), collectedPost.createdDate.desc())
                .fetch();

        List<Collection> content = new ArrayList<>(collects).stream()
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
    public Slice<Post> findPostsByFollowingMemberOrderByIdSliced(Pageable pageable, List<Long> followedMemberIds) {

        List<Post> content = jpaQueryFactory
                .select(post)
                .from(post)
                .join(post.member, member).fetchJoin()
                .where(member.id.in(followedMemberIds))
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
    public Slice<Collection> findCollectionsByFollowedMemberOrderByIdSliced(Pageable pageable, List<Long> followedMemberIds) {

        List<Collection> content = jpaQueryFactory
                .select(collection)
                .from(collection)
                .join(collection.member, member).fetchJoin()
                .where(member.id.in(followedMemberIds))
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
    public Slice<Post> findPostsByFollowedHashtagOrderByIdSliced(Pageable pageable, List<Hashtag> followedHashtags) {

        List<Post> posts = jpaQueryFactory
                .select(post)
                .from(post)
                .join(post.member, member).fetchJoin()
                .join(post.postedHashtags, postedHashtag).fetchJoin()
                .where(postedHashtag.hashtag.in(followedHashtags))
//                .join(postedHashtag.hashtag, hashtag).fetchJoin()
//                .where(hashtag.id.in(followedHashtagIds))
//                .offset(pageable.getOffset())
//                .limit(pageable.getPageSize() + 1)
                .orderBy(post.id.desc())
                .fetch();

        List<Post> content = new ArrayList<>(posts)
                .stream()
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
    public Slice<Post> findPostsByFollowedMovieOrderByIdSliced(Pageable pageable, List<Movie> followedMovies) {
        return null;
    }
}
