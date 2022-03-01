package com.nameless.spin_off.service.query;

import com.nameless.spin_off.dto.MovieDto.SearchPageAtAllMovieDto;
import com.nameless.spin_off.repository.query.MovieQueryRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Slice;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class MovieQueryServiceJpa implements MovieQueryService{

    private final MovieQueryRepository movieQueryRepository;

    @Override
    public Slice<SearchPageAtAllMovieDto> getSearchPageMovieAtAllSliced(String keyword, Pageable pageable) {
        return movieQueryRepository.findAllSlicedForSearchPageAtAll(keyword, pageable);
    }
}
