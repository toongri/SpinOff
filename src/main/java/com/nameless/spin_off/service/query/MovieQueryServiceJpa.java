package com.nameless.spin_off.service.query;

import com.nameless.spin_off.controller.api.MovieApiController.MovieSearchResult;
import com.nameless.spin_off.dto.HashtagDto.RelatedMostTaggedHashtagDto;
import com.nameless.spin_off.dto.MovieDto.SearchPageAtAllMovieDto;
import com.nameless.spin_off.dto.MovieDto.SearchPageAtMovieMovieDto;
import com.nameless.spin_off.dto.MovieDto.SearchPageAtMovieMovieFirstDto;
import com.nameless.spin_off.entity.movie.Movie;
import com.nameless.spin_off.exception.movie.NotExistMovieException;
import com.nameless.spin_off.repository.movie.MovieRepository;
import com.nameless.spin_off.repository.query.HashtagQueryRepository;
import com.nameless.spin_off.repository.query.MovieQueryRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Slice;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class MovieQueryServiceJpa implements MovieQueryService{

    private final MovieQueryRepository movieQueryRepository;
    private final MovieRepository movieRepository;
    private final HashtagQueryRepository hashtagQueryRepository;

    @Override
    public Slice<SearchPageAtAllMovieDto> getSearchPageMovieAtAllSliced(String keyword, Pageable pageable) {
        return movieQueryRepository.findAllSlicedForSearchPageAtAll(keyword, pageable);
    }

    @Override
    public Slice<SearchPageAtMovieMovieDto> getSearchPageMovieAtMovieSliced(String keyword, Pageable pageable) {
        return movieQueryRepository.findAllSlicedForSearchPageAtMovie(keyword, pageable);
    }

    @Override
    public MovieSearchResult getSearchPageMovieAtMovieSlicedFirst(String keyword, Pageable pageable, int length)
            throws NotExistMovieException {

        Slice<SearchPageAtMovieMovieDto> movies =
                movieQueryRepository.findAllSlicedForSearchPageAtMovie(keyword, pageable);

        SearchPageAtMovieMovieFirstDto first = getMovieForSearchPageFirst(movies);

        List<RelatedMostTaggedHashtagDto> hashtags = hashtagQueryRepository.findAllByMovieIds(
                length, movies.getContent().stream()
                        .map(SearchPageAtMovieMovieDto::getMovieId)
                        .collect(Collectors.toList()));

        return new MovieSearchResult(first, movies, hashtags);
    }

    private SearchPageAtMovieMovieFirstDto getMovieForSearchPageFirst(Slice<SearchPageAtMovieMovieDto> movies)
            throws NotExistMovieException {

        if (!movies.isEmpty()) {
            Optional<Movie> optionalMovie =
                    movieRepository.findOneByIdWithTaggedPost(movies.getContent().get(0).getMovieId());

            Movie movie = optionalMovie.orElseThrow(NotExistMovieException::new);

            return new SearchPageAtMovieMovieFirstDto(movie);

        } else {
            return null;
        }
    }
}
