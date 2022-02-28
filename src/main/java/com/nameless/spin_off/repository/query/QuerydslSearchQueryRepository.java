package com.nameless.spin_off.repository.query;

import com.nameless.spin_off.dto.CollectionDto.RelatedSearchCollectionDto;
import com.nameless.spin_off.dto.HashtagDto.MostPopularHashtag;
import com.nameless.spin_off.dto.HashtagDto.RelatedSearchHashtagDto;
import com.nameless.spin_off.dto.MemberDto.RelatedSearchMemberDto;
import com.nameless.spin_off.dto.MovieDto.RelatedSearchMovieDto;
import com.nameless.spin_off.dto.PostDto.RelatedSearchPostDto;
import com.nameless.spin_off.dto.*;
import com.nameless.spin_off.dto.SearchDto.LastSearchDto;
import com.querydsl.jpa.impl.JPAQueryFactory;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

import java.util.List;

import static com.nameless.spin_off.entity.collection.QCollection.collection;
import static com.nameless.spin_off.entity.hashtag.QHashtag.hashtag;
import static com.nameless.spin_off.entity.member.QMember.member;
import static com.nameless.spin_off.entity.member.QSearchedByMember.searchedByMember;
import static com.nameless.spin_off.entity.movie.QMovie.movie;
import static com.nameless.spin_off.entity.post.QPost.post;

@Repository
@RequiredArgsConstructor
public class QuerydslSearchQueryRepository implements SearchQueryRepository {

    private final JPAQueryFactory jpaQueryFactory;

    @Override
    public List<RelatedSearchMemberDto> findRelatedMembersAboutKeyword(String keyword, int length) {
        return jpaQueryFactory
                .select(new QMemberDto_RelatedSearchMemberDto(
                        member.id, member.profileImg, member.nickname, member.accountId))
                .from(member)
                .where(member.nickname.contains(keyword))
                .orderBy(member.popularity.desc())
                .limit(length)
                .fetch();
    }

    @Override
    public List<RelatedSearchPostDto> findRelatedPostsAboutKeyword(String keyword, int length) {
        return jpaQueryFactory
                .select(new QPostDto_RelatedSearchPostDto(
                        post.id, post.title))
                .from(post)
                .where(post.title.contains(keyword))
                .orderBy(post.popularity.desc())
                .limit(length)
                .fetch();
    }

    @Override
    public List<RelatedSearchHashtagDto> findRelatedHashtagsAboutKeyword(String keyword, int length) {
        return jpaQueryFactory
                .select(new QHashtagDto_RelatedSearchHashtagDto(
                        hashtag.id, hashtag.content))
                .from(hashtag)
                .where(hashtag.content.contains(keyword))
                .orderBy(hashtag.popularity.desc())
                .limit(length)
                .fetch();
    }

    @Override
    public List<RelatedSearchCollectionDto> findRelatedCollectionsAboutKeyword(String keyword, int length) {
        return jpaQueryFactory
                .select(new QCollectionDto_RelatedSearchCollectionDto(
                        collection.id, collection.title))
                .from(collection)
                .where(collection.title.contains(keyword))
                .orderBy(collection.popularity.desc())
                .limit(length)
                .fetch();
    }

    @Override
    public List<RelatedSearchMovieDto> findRelatedMoviesAboutKeyword(String keyword, int length) {
        return jpaQueryFactory
                .select(new QMovieDto_RelatedSearchMovieDto(
                        movie.id, movie.title, movie.imageUrl))
                .from(movie)
                .where(movie.title.contains(keyword))
                .orderBy(movie.popularity.desc())
                .limit(length)
                .fetch();
    }

    @Override
    public List<MostPopularHashtag> findMostPopularHashtagsLimit(int length) {
        return jpaQueryFactory
                .select(new QHashtagDto_MostPopularHashtag(
                        hashtag.id, hashtag.content))
                .from(hashtag)
                .orderBy(hashtag.popularity.desc())
                .limit(length)
                .fetch();
    }

    @Override
    public List<LastSearchDto> findLastSearchesByMemberIdLimit(Long id, int length) {
        return jpaQueryFactory
                .select(new QSearchDto_LastSearchDto(
                        searchedByMember.id, searchedByMember.content))
                .from(searchedByMember)
                .join(searchedByMember.member, member)
                .orderBy(searchedByMember.id.desc())
                .where(member.id.eq(id))
                .limit(length)
                .fetch();
    }

}
