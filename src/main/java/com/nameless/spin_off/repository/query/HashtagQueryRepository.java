package com.nameless.spin_off.repository.query;

import com.nameless.spin_off.dto.HashtagDto;
import com.nameless.spin_off.dto.QHashtagDto_PopularityRelatedHashtagDto;
import com.nameless.spin_off.entity.hashtag.Hashtag;
import com.nameless.spin_off.repository.support.Querydsl4RepositorySupport;
import com.querydsl.core.types.dsl.Expressions;
import com.querydsl.core.types.dsl.NumberPath;
import org.springframework.stereotype.Repository;

import java.util.List;

import static com.nameless.spin_off.entity.hashtag.QHashtag.hashtag;
import static com.nameless.spin_off.entity.hashtag.QPostedHashtag.postedHashtag;

@Repository
public class HashtagQueryRepository extends Querydsl4RepositorySupport {

    public HashtagQueryRepository() {
        super(Hashtag.class);
    }

    public List<HashtagDto.RelatedMostTaggedHashtagDto> findAllByPostIds(int length, List<Long> postIds) {
        NumberPath<Long> aliasQuantity = Expressions.numberPath(Long.class, "quantity");

        return getQueryFactory()
                .select(new QHashtagDto_PopularityRelatedHashtagDto(
                        hashtag.id, hashtag.content, postedHashtag.hashtag.count().as(aliasQuantity)))
                .from(postedHashtag)
                .join(postedHashtag.hashtag, hashtag)
                .where(postedHashtag.post.id.in(postIds))
                .groupBy(postedHashtag.hashtag)
                .orderBy(aliasQuantity.desc(), hashtag.popularity.desc())
                .limit(length)
                .fetch();
    }

    public List<HashtagDto.RelatedMostTaggedHashtagDto> findAllByMemberIds(int length, List<Long> memberIds) {
        NumberPath<Long> aliasQuantity = Expressions.numberPath(Long.class, "quantity");

        return getQueryFactory()
                .select(new QHashtagDto_PopularityRelatedHashtagDto(
                        hashtag.id, hashtag.content, postedHashtag.hashtag.count().as(aliasQuantity)))
                .from(postedHashtag)
                .join(postedHashtag.hashtag, hashtag)
                .where(postedHashtag.post.member.id.in(memberIds))
                .groupBy(postedHashtag.hashtag)
                .orderBy(aliasQuantity.desc(), hashtag.popularity.desc())
                .limit(length)
                .fetch();
    }
}
