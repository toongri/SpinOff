package com.nameless.spin_off.service.query;

import com.nameless.spin_off.dto.MovieDto;
import com.nameless.spin_off.dto.MovieDto.SearchPageAtAllMovieDto;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Slice;

public interface MovieQueryService {
    Slice<SearchPageAtAllMovieDto> getSearchPageMovieAtAllSliced(String keyword, Pageable pageable);
}
