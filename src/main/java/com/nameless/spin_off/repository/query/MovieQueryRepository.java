package com.nameless.spin_off.repository.query;

import com.nameless.spin_off.dto.MemberDto.MembersByContentDto;
import com.nameless.spin_off.dto.MemberDto.MovieMemberDto;
import com.nameless.spin_off.dto.MovieDto.FollowMovieDto;
import com.nameless.spin_off.dto.MovieDto.ReadMovieDto;
import com.nameless.spin_off.dto.MovieDto.SearchAllMovieDto;
import com.nameless.spin_off.dto.MovieDto.SearchMovieDto;
import com.nameless.spin_off.dto.*;
import com.nameless.spin_off.entity.movie.Movie;
import com.nameless.spin_off.repository.support.Querydsl4RepositorySupport;
import com.querydsl.core.types.dsl.BooleanExpression;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Slice;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import static com.nameless.spin_off.entity.member.QMember.member;
import static com.nameless.spin_off.entity.movie.QFollowedMovie.followedMovie;
import static com.nameless.spin_off.entity.movie.QMovie.movie;
import static com.nameless.spin_off.entity.movie.QViewedMovieByIp.viewedMovieByIp;
import static com.nameless.spin_off.entity.post.QPost.post;
import static com.nameless.spin_off.enums.movie.MovieApiEnum.KOBIS_API_REQUEST_SIZE_MAX;
import static com.nameless.spin_off.enums.movie.MovieApiEnum.NAVER_API_REQUEST_NUMBER_MAX;

@Repository
public class MovieQueryRepository extends Querydsl4RepositorySupport {

    public MovieQueryRepository() {
        super(Movie.class);
    }

    public List<Movie> findAllWithoutActorOrderByCreateDesc(int page, int size) {
        return getQueryFactory()
                .select(movie)
                .from(movie)
                .where(movie.actors.isEmpty())
                .orderBy(movie.createdDate.desc())
                .offset((long) page * size * KOBIS_API_REQUEST_SIZE_MAX.getValue())
                .limit((long) size * KOBIS_API_REQUEST_SIZE_MAX.getValue())
                .fetch();
    }

    public List<Movie> findAllWithoutNaverInfoOrderByCreateDesc() {
        return getQueryFactory()
                .select(movie)
                .from(movie)
                .where(movie.naverUrl.isEmpty().or(movie.thumbnail.eq("")))
                .orderBy(movie.createdDate.desc())
                .limit(NAVER_API_REQUEST_NUMBER_MAX.getValue())
                .fetch();
    }

    public List<MovieMemberDto> findMembersByMovieId(Long movieId, List<Long> blockedMemberIds) {
        return getQueryFactory()
                .select(new QMemberDto_MovieMemberDto(
                        member.id, member.nickname, member.profileImg))
                .from(member)
                .join(member.posts, post)
                .join(post.movie, movie)
                .where(memberNotIn(blockedMemberIds), movie.id.eq(movieId))
                .groupBy(member)
                .orderBy(post.popularity.max().desc())
                .limit(3)
                .fetch();
    }

    public Optional<ReadMovieDto> findByIdForRead(Long movieId, List<Long> blockedMemberIds) {
        return Optional.ofNullable(getQueryFactory()
                .select(new QMovieDto_ReadMovieDto(
                        movie.id, movie.title, movie.thumbnail, movie.naverUrl, movie.nation, movie.directorName,
                        movie.actors, movie.firstGenreOfMovieStatus, movie.secondGenreOfMovieStatus,
                        movie.thirdGenreOfMovieStatus, movie.fourthGenreOfMovieStatus, post.countDistinct(),
                        followedMovie.countDistinct()))
                .from(movie)
                .leftJoin(movie.taggedPosts, post)
                .on(post.member.id.notIn(blockedMemberIds))
                .leftJoin(movie.followingMembers, followedMovie)
                .on(followedMovie.member.id.notIn(blockedMemberIds))
                .groupBy(movie)
                .where(movie.id.eq(movieId))
                .fetchFirst());

    }

    public List<FollowMovieDto> findAllFollowMoviesByMemberId(Long memberId) {
        return getQueryFactory()
                .select(new QMovieDto_FollowMovieDto(
                        movie.id, movie.title, movie.directorName))
                .from(followedMovie)
                .join(followedMovie.movie, movie)
                .where(
                        followedMovie.member.id.eq(memberId))
                .orderBy(followedMovie.id.desc())
                .fetch();
    }

    public List<MembersByContentDto> findAllFollowMemberByMovieId(Long movieId, List<Long> blockedMemberIds) {
        return getQueryFactory()
                .select(new QMemberDto_MembersByContentDto(
                        member.id, member.profileImg, member.nickname, member.accountId))
                .from(followedMovie)
                .join(followedMovie.member, member)
                .where(
                        memberNotIn(blockedMemberIds),
                        followedMovie.movie.id.eq(movieId))
                .orderBy(followedMovie.id.desc())
                .fetch();
    }

    public Boolean isExist(Long id) {
        Integer fetchOne = getQueryFactory()
                .selectOne()
                .from(movie)
                .where(movie.id.eq(id))
                .fetchFirst();

        return fetchOne != null;
    }

    public Boolean isExistFollowedMovie(Long memberId, Long movieId) {
        Integer fetchOne = getQueryFactory()
                .selectOne()
                .from(followedMovie)
                .where(
                        followedMovie.member.id.eq(memberId),
                        followedMovie.movie.id.eq(movieId))
                .fetchFirst();

        return fetchOne != null;
    }

    public Boolean isExistIp(Long id, String ip, LocalDateTime time) {
        Integer fetchOne = getQueryFactory()
                .selectOne()
                .from(viewedMovieByIp)
                .where(
                        viewedMovieByIp.ip.eq(ip),
                        viewedMovieByIp.movie.id.eq(id),
                        viewedMovieByIp.createdDate.after(time))
                .fetchFirst();

        return fetchOne != null;
    }

    public Slice<SearchAllMovieDto> findAllSlicedForSearchPageAtAll(String keyword, Pageable pageable) {
        return applySlicing(pageable, contentQuery -> contentQuery
                .select(new QMovieDto_SearchAllMovieDto(
                        movie.id, movie.title, movie.thumbnail,
                        movie.firstGenreOfMovieStatus, movie.secondGenreOfMovieStatus))
                .from(movie)
                .where(movie.title.contains(keyword)));
    }

    public Slice<SearchMovieDto> findAllSlicedForSearchPageAtMovie(String keyword, Pageable pageable) {
        return applySlicing(pageable, contentQuery -> contentQuery
                .select(new QMovieDto_SearchMovieDto(
                        movie.id, movie.title, movie.thumbnail))
                .from(movie)
                .where(movie.title.contains(keyword)));
    }

    public List<Movie> findAllByViewAfterTime(LocalDateTime time) {
        return getQueryFactory()
                .selectFrom(movie).distinct()
                .join(movie.viewedMovieByIps, viewedMovieByIp).fetchJoin()
                .where(
                        viewedMovieByIp.createdDate.after(time))
                .fetch();
    }
    private BooleanExpression memberNotIn(List<Long> memberIds) {
        return memberIds.isEmpty() ? null : member.id.notIn(memberIds);
    }
}
