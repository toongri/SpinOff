package com.nameless.spin_off.repository.query;

import com.nameless.spin_off.dto.CollectionDto.RelatedSearchCollectionDto;
import com.nameless.spin_off.dto.HashtagDto.MostPopularHashtag;
import com.nameless.spin_off.dto.HashtagDto.RelatedSearchHashtagDto;
import com.nameless.spin_off.dto.MemberDto.RelatedSearchMemberDto;
import com.nameless.spin_off.dto.MovieDto.RelatedSearchMovieDto;
import com.nameless.spin_off.dto.PostDto.RelatedSearchPostDto;
import com.nameless.spin_off.dto.*;
import com.querydsl.jpa.impl.JPAQueryFactory;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

import java.util.List;

import static com.nameless.spin_off.StaticVariable.MOST_POPULAR_HASHTAG_NUMBER;
import static com.nameless.spin_off.StaticVariable.RELATED_SEARCH_NUMBER;
import static com.nameless.spin_off.entity.collection.QCollection.collection;
import static com.nameless.spin_off.entity.hashtag.QHashtag.hashtag;
import static com.nameless.spin_off.entity.member.QMember.member;
import static com.nameless.spin_off.entity.movie.QMovie.movie;
import static com.nameless.spin_off.entity.post.QPost.post;

@Repository
@RequiredArgsConstructor
public class QuerydslSearchQueryRepository implements SearchQueryRepository{

    private final JPAQueryFactory jpaQueryFactory;

    @Override
    public List<RelatedSearchMemberDto> getMembersAboutKeyword(String keyword) {

        return jpaQueryFactory
                .select(new QMemberDto_RelatedSearchMemberDto(
                        member.id, member.profileImg, member.nickname, member.accountId))
                .from(member)
                .where(member.nickname.contains(keyword))
                .orderBy(member.popularity.desc())
                .limit(RELATED_SEARCH_NUMBER)
                .fetch();
    }

    @Override
    public List<RelatedSearchPostDto> getPostsAboutKeyword(String keyword) {
        return jpaQueryFactory
                .select(new QPostDto_RelatedSearchPostDto(
                        post.id, post.title))
                .from(post)
                .where(post.title.contains(keyword))
                .orderBy(post.popularity.desc())
                .limit(RELATED_SEARCH_NUMBER)
                .fetch();
    }

    @Override
    public List<RelatedSearchHashtagDto> getHashtagsAboutKeyword(String keyword) {
        return jpaQueryFactory
                .select(new QHashtagDto_RelatedSearchHashtagDto(
                        hashtag.id, hashtag.content))
                .from(hashtag)
                .where(hashtag.content.contains(keyword))
                .orderBy(hashtag.popularity.desc())
                .limit(RELATED_SEARCH_NUMBER)
                .fetch();
    }

    @Override
    public List<RelatedSearchCollectionDto> getCollectionsAboutKeyword(String keyword) {
        return jpaQueryFactory
                .select(new QCollectionDto_RelatedSearchCollectionDto(
                        collection.id, collection.title))
                .from(collection)
                .where(collection.title.contains(keyword))
                .orderBy(collection.popularity.desc())
                .limit(RELATED_SEARCH_NUMBER)
                .fetch();
    }

    @Override
    public List<RelatedSearchMovieDto> getMoviesAboutKeyword(String keyword) {
        return jpaQueryFactory
                .select(new QMovieDto_RelatedSearchMovieDto(
                        movie.id, movie.title))
                .from(movie)
                .where(movie.title.contains(keyword))
                .orderBy(movie.popularity.desc())
                .limit(RELATED_SEARCH_NUMBER)
                .fetch();
    }

    @Override
    public List<MostPopularHashtag> getMostPopularHashtags() {
        return jpaQueryFactory
                .select(new QHashtagDto_MostPopularHashtag(
                        hashtag.id, hashtag.content))
                .from(hashtag)
                .orderBy(hashtag.popularity.desc())
                .limit(MOST_POPULAR_HASHTAG_NUMBER)
                .fetch();
    }

}
