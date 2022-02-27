package com.nameless.spin_off.repository.query;

import com.nameless.spin_off.dto.*;
import com.nameless.spin_off.dto.CollectionDto.RelatedSearchCollectionDto;
import com.nameless.spin_off.dto.CollectionDto.SearchPageAtAllCollectionDto;
import com.nameless.spin_off.dto.HashtagDto.MostPopularHashtag;
import com.nameless.spin_off.dto.HashtagDto.RelatedSearchHashtagDto;
import com.nameless.spin_off.dto.MemberDto.RelatedSearchMemberDto;
import com.nameless.spin_off.dto.MemberDto.SearchPageAtAllMemberDto;
import com.nameless.spin_off.dto.MovieDto.RelatedSearchMovieDto;
import com.nameless.spin_off.dto.MovieDto.SearchPageAtAllMovieDto;
import com.nameless.spin_off.dto.PostDto.RelatedSearchPostDto;
import com.nameless.spin_off.dto.PostDto.SearchPageAtAllPostDto;
import com.nameless.spin_off.dto.SearchDto.LastSearchDto;
import com.nameless.spin_off.entity.collection.Collection;
import com.nameless.spin_off.entity.member.Member;
import com.querydsl.jpa.impl.JPAQueryFactory;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Slice;
import org.springframework.data.domain.SliceImpl;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.stream.Collectors;

import static com.nameless.spin_off.entity.collection.QCollection.collection;
import static com.nameless.spin_off.entity.enums.search.RelatedSearchEnum.LAST_SEARCH_NUMBER;
import static com.nameless.spin_off.entity.enums.search.RelatedSearchEnum.MOST_POPULAR_HASHTAG_NUMBER;
import static com.nameless.spin_off.entity.hashtag.QHashtag.hashtag;
import static com.nameless.spin_off.entity.member.QMember.member;
import static com.nameless.spin_off.entity.member.QSearchedByMember.searchedByMember;
import static com.nameless.spin_off.entity.movie.QMovie.movie;
import static com.nameless.spin_off.entity.post.QPost.post;

@Repository
@RequiredArgsConstructor
public class QuerydslSearchQueryRepository implements SearchQueryRepository{

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
    public List<MostPopularHashtag> findMostPopularHashtags() {
        return jpaQueryFactory
                .select(new QHashtagDto_MostPopularHashtag(
                        hashtag.id, hashtag.content))
                .from(hashtag)
                .orderBy(hashtag.popularity.desc())
                .limit(MOST_POPULAR_HASHTAG_NUMBER.getValue())
                .fetch();
    }

    @Override
    public List<LastSearchDto> findLastSearchesByMemberId(Long id) {
        return jpaQueryFactory
                .select(new QSearchDto_LastSearchDto(
                        searchedByMember.id, searchedByMember.content))
                .from(searchedByMember)
                .join(searchedByMember.member, member)
                .orderBy(searchedByMember.id.desc())
                .where(member.id.eq(id))
                .limit(LAST_SEARCH_NUMBER.getValue())
                .fetch();
    }

    @Override
    public Slice<SearchPageAtAllMemberDto> findSearchPageMemberAtAllSliced(String keyword, Pageable pageable) {

        List<SearchPageAtAllMemberDto> content = jpaQueryFactory
                .select(new QMemberDto_SearchPageAtAllMemberDto(
                        member.id, member.profileImg, member.nickname, member.accountId))
                .from(member)
                .where(member.nickname.contains(keyword))
                .orderBy(member.popularity.desc())
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
    public Slice<SearchPageAtAllMovieDto> findSearchPageMovieAtAllSliced(String keyword, Pageable pageable) {
        List<SearchPageAtAllMovieDto> content = jpaQueryFactory
                .select(new QMovieDto_SearchPageAtAllMovieDto(
                        movie.id, movie.title, movie.imageUrl,
                        movie.firstGenreOfMovieStatus, movie.secondGenreOfMovieStatus))
                .from(movie)
                .where(movie.title.contains(keyword))
                .orderBy(movie.popularity.desc())
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
    public Slice<SearchPageAtAllCollectionDto> findSearchPageCollectionAtAllSliced(String keyword, Pageable pageable,
                                                                                   List<Member> followedMembers) {

        List<Collection> content = jpaQueryFactory
                .selectFrom(collection)
                .join(collection.member, member).fetchJoin()
                .where(collection.title.contains(keyword))
                .orderBy(collection.popularity.desc())
                .offset(pageable.getOffset())
                .limit(pageable.getPageSize() + 1)
                .fetch();

        boolean hasNext = false;
        if (content.size() > pageable.getPageSize()) {
            content.remove(pageable.getPageSize());
            hasNext = true;
        }
        if (followedMembers.isEmpty()) {
            return new SliceImpl<>(content.stream().map(SearchPageAtAllCollectionDto::new)
                    .collect(Collectors.toList()), pageable, hasNext);
        } else {
            return new SliceImpl<>(
                    content.stream().map(c -> new CollectionDto.SearchPageAtAllCollectionDto(c, followedMembers))
                            .collect(Collectors.toList()), pageable, hasNext);
        }
    }

    @Override
    public Slice<SearchPageAtAllPostDto> findSearchPagePostAtAllSliced(String keyword, Pageable pageable) {
        List<SearchPageAtAllPostDto> content = jpaQueryFactory
                .select(new QPostDto_SearchPageAtAllPostDto(
                        post.id, post.title, member.id, member.nickname, member.profileImg, post.thumbnailUrl))
                .from(post)
                .join(post.member, member)
                .where(post.title.contains(keyword))
                .orderBy(post.popularity.desc())
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

}
