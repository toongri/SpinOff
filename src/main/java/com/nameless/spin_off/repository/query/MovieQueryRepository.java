package com.nameless.spin_off.repository.query;

import com.nameless.spin_off.dto.MovieDto.SearchPageAtAllMovieDto;
import com.nameless.spin_off.dto.MovieDto.SearchPageAtMovieMovieDto;
import com.nameless.spin_off.dto.QMovieDto_SearchPageAtAllMovieDto;
import com.nameless.spin_off.dto.QMovieDto_SearchPageAtMovieMovieDto;
import com.nameless.spin_off.entity.movie.Movie;
import com.nameless.spin_off.repository.support.Querydsl4RepositorySupport;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Slice;
import org.springframework.stereotype.Repository;

import static com.nameless.spin_off.entity.movie.QMovie.movie;

@Repository
public class MovieQueryRepository extends Querydsl4RepositorySupport {

    public MovieQueryRepository() {
        super(Movie.class);
    }

    public Slice<SearchPageAtAllMovieDto> findAllSlicedForSearchPageAtAll(String keyword, Pageable pageable) {
        return applySlicing(pageable, contentQuery -> contentQuery
                .select(new QMovieDto_SearchPageAtAllMovieDto(
                        movie.id, movie.title, movie.imageUrl,
                        movie.firstGenreOfMovieStatus, movie.secondGenreOfMovieStatus))
                .from(movie)
                .where(movie.title.contains(keyword)));
    }

    public Slice<SearchPageAtMovieMovieDto> findAllSlicedForSearchPageAtMovie(String keyword, Pageable pageable) {
        return applySlicing(pageable, contentQuery -> contentQuery
                .select(new QMovieDto_SearchPageAtMovieMovieDto(
                        movie.id, movie.title, movie.imageUrl))
                .from(movie)
                .where(movie.title.contains(keyword)));
    }

}
