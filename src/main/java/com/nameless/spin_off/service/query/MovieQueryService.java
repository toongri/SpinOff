package com.nameless.spin_off.service.query;

import com.nameless.spin_off.controller.api.MovieApiController.MovieSearchResult;
import com.nameless.spin_off.dto.MovieDto.SearchPageAtAllMovieDto;
import com.nameless.spin_off.dto.MovieDto.SearchPageAtMovieMovieDto;
import com.nameless.spin_off.exception.movie.NotExistMovieException;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Slice;

public interface MovieQueryService {
    Slice<SearchPageAtAllMovieDto> getSearchPageMovieAtAllSliced(String keyword, Pageable pageable);
    Slice<SearchPageAtMovieMovieDto> getSearchPageMovieAtMovieSliced(String keyword, Pageable pageable);
    MovieSearchResult getSearchPageMovieAtMovieSlicedFirst(String keyword, Pageable pageable, int length) throws NotExistMovieException;
}
