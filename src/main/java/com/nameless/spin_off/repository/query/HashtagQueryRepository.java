package com.nameless.spin_off.repository.query;

import com.nameless.spin_off.dto.HashtagDto.RelatedMostTaggedHashtagDto;
import com.nameless.spin_off.dto.QHashtagDto_RelatedMostTaggedHashtagDto;
import com.nameless.spin_off.entity.hashtag.Hashtag;
import com.nameless.spin_off.repository.support.Querydsl4RepositorySupport;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;

import static com.nameless.spin_off.entity.collection.QCollectedPost.collectedPost;
import static com.nameless.spin_off.entity.collection.QCollection.collection;
import static com.nameless.spin_off.entity.hashtag.QFollowedHashtag.followedHashtag;
import static com.nameless.spin_off.entity.hashtag.QHashtag.hashtag;
import static com.nameless.spin_off.entity.hashtag.QPostedHashtag.postedHashtag;
import static com.nameless.spin_off.entity.hashtag.QViewedHashtagByIp.viewedHashtagByIp;
import static com.nameless.spin_off.entity.movie.QMovie.movie;
import static com.nameless.spin_off.entity.post.QPost.post;

@Repository
public class HashtagQueryRepository extends Querydsl4RepositorySupport {

    public HashtagQueryRepository() {
        super(Hashtag.class);
    }

    public Boolean isExist(Long id) {
        Integer fetchOne = getQueryFactory()
                .selectOne()
                .from(hashtag)
                .where(hashtag.id.eq(id))
                .fetchFirst();

        return fetchOne != null;
    }

    public Boolean isExistFollowedHashtag(Long memberId, Long hashtagId) {
        Integer fetchOne = getQueryFactory()
                .selectOne()
                .from(followedHashtag)
                .where(
                        followedHashtag.member.id.eq(memberId),
                        followedHashtag.hashtag.id.eq(hashtagId))
                .fetchFirst();

        return fetchOne != null;
    }

    public Boolean isExistIp(Long id, String ip, LocalDateTime time) {
        Integer fetchOne = getQueryFactory()
                .selectOne()
                .from(viewedHashtagByIp)
                .where(
                        viewedHashtagByIp.ip.eq(ip),
                        viewedHashtagByIp.hashtag.id.eq(id),
                        viewedHashtagByIp.createdDate.after(time))
                .fetchFirst();

        return fetchOne != null;
    }

    public List<RelatedMostTaggedHashtagDto> findAllByPostIds(int length, List<Long> postIds) {

        return getQueryFactory()
                .select(new QHashtagDto_RelatedMostTaggedHashtagDto(
                        hashtag.id, hashtag.content))
                .from(postedHashtag)
                .join(postedHashtag.hashtag, hashtag)
                .where(postedHashtag.post.id.in(postIds))
                .groupBy(postedHashtag.hashtag)
                .orderBy(postedHashtag.hashtag.count().desc(), hashtag.popularity.desc())
                .limit(length)
                .fetch();
    }

    public List<RelatedMostTaggedHashtagDto> findAllByMemberIds(int length, List<Long> memberIds) {

        return getQueryFactory()
                .select(new QHashtagDto_RelatedMostTaggedHashtagDto(
                        hashtag.id, hashtag.content))
                .from(postedHashtag)
                .join(postedHashtag.hashtag, hashtag)
                .where(postedHashtag.post.member.id.in(memberIds))
                .groupBy(postedHashtag.hashtag)
                .orderBy(postedHashtag.hashtag.count().desc(), hashtag.popularity.desc())
                .limit(length)
                .fetch();
    }


    public List<RelatedMostTaggedHashtagDto> findAllByCollectionIds(int length, List<Long> collectionIds) {

        return getQueryFactory()
                .select(new QHashtagDto_RelatedMostTaggedHashtagDto(
                        hashtag.id, hashtag.content))
                .from(collection)
                .join(collection.collectedPosts, collectedPost)
                .join(collectedPost.post, post)
                .join(post.postedHashtags, postedHashtag)
                .join(postedHashtag.hashtag, hashtag)
                .where(collection.id.in(collectionIds))
                .groupBy(postedHashtag.hashtag)
                .orderBy(postedHashtag.hashtag.count().desc(), hashtag.popularity.desc())
                .limit(length)
                .fetch();
    }

    public List<RelatedMostTaggedHashtagDto> findAllByMovieIds(int length, List<Long> movieIds) {

        return getQueryFactory()
                .select(new QHashtagDto_RelatedMostTaggedHashtagDto(
                        hashtag.id, hashtag.content))
                .from(movie)
                .join(movie.taggedPosts, post)
                .join(post.postedHashtags, postedHashtag)
                .join(postedHashtag.hashtag, hashtag)
                .where(movie.id.in(movieIds))
                .groupBy(postedHashtag.hashtag)
                .orderBy(postedHashtag.hashtag.count().desc(), hashtag.popularity.desc())
                .limit(length)
                .fetch();
    }

    public List<Hashtag> findAllByViewAfterTime(LocalDateTime time) {
        return getQueryFactory()
                .selectFrom(hashtag)
                .join(hashtag.viewedHashtagByIps, viewedHashtagByIp).fetchJoin()
                .where(
                        viewedHashtagByIp.createdDate.after(time))
                .fetch();
    }
}
