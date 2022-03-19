package com.nameless.spin_off.repository.query;

import com.nameless.spin_off.dto.MovieDto.SearchAllMovieDto;
import com.nameless.spin_off.dto.MovieDto.SearchMovieDto;
import com.nameless.spin_off.dto.QMovieDto_SearchAllMovieDto;
import com.nameless.spin_off.dto.QMovieDto_SearchMovieDto;
import com.nameless.spin_off.entity.movie.Movie;
import com.nameless.spin_off.repository.support.Querydsl4RepositorySupport;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Slice;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;

import static com.nameless.spin_off.entity.movie.QFollowedMovie.followedMovie;
import static com.nameless.spin_off.entity.movie.QMovie.movie;
import static com.nameless.spin_off.entity.movie.QViewedMovieByIp.viewedMovieByIp;

@Repository
public class MovieQueryRepository extends Querydsl4RepositorySupport {

    public MovieQueryRepository() {
        super(Movie.class);
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
                        movie.id, movie.title, movie.imageUrl,
                        movie.firstGenreOfMovieStatus, movie.secondGenreOfMovieStatus))
                .from(movie)
                .where(movie.title.contains(keyword)));
    }

    public Slice<SearchMovieDto> findAllSlicedForSearchPageAtMovie(String keyword, Pageable pageable) {
        return applySlicing(pageable, contentQuery -> contentQuery
                .select(new QMovieDto_SearchMovieDto(
                        movie.id, movie.title, movie.imageUrl))
                .from(movie)
                .where(movie.title.contains(keyword)));
    }

    public List<Movie> findAllByViewAfterTime(LocalDateTime time) {
        return getQueryFactory()
                .selectFrom(movie)
                .join(movie.viewedMovieByIps, viewedMovieByIp).fetchJoin()
                .where(
                        viewedMovieByIp.createdDate.after(time))
                .fetch();
    }
}
