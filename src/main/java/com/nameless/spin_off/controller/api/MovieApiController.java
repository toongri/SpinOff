package com.nameless.spin_off.controller.api;

import com.nameless.spin_off.dto.HashtagDto.RelatedMostTaggedHashtagDto;
import com.nameless.spin_off.dto.MovieDto.SearchPageAtMovieMovieDto;
import com.nameless.spin_off.dto.MovieDto.SearchPageAtMovieMovieFirstDto;
import com.nameless.spin_off.exception.member.AlreadyFollowedMovieException;
import com.nameless.spin_off.exception.member.NotExistMemberException;
import com.nameless.spin_off.exception.movie.NotExistMovieException;
import com.nameless.spin_off.service.movie.MovieService;
import com.nameless.spin_off.service.query.HashtagQueryService;
import com.nameless.spin_off.service.query.MovieQueryService;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Slice;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequiredArgsConstructor
@RequestMapping(value = "/api/movie")
public class MovieApiController {

    private final MovieService movieService;
    private final HashtagQueryService hashtagQueryService;
    private final MovieQueryService movieQueryService;

    @PostMapping("/view")
    public MovieResult<Long> createViewOne(@RequestBody String ip, @RequestBody Long postId) throws NotExistMovieException {
        Long aLong = movieService.insertViewedMovieByIp(ip, postId);

        return new MovieResult<Long>(aLong);
    }

    @PostMapping("/follow")
    public MovieResult<Long> createFollowOne(@RequestParam Long memberId, @RequestParam Long hashtagId)
            throws NotExistMovieException, AlreadyFollowedMovieException, NotExistMemberException {
        Long likedHashtagId = movieService.insertFollowedMovieByMovieId(memberId, hashtagId);

        return new MovieResult<Long>(likedHashtagId);
    }

    @GetMapping("/search")
    public MovieResult getSearchPageMovieAtMovieSliced(
            @RequestParam String keyword, @RequestParam int hashtagLength,
            @PageableDefault(sort = "popularity", direction = Sort.Direction.DESC) Pageable pageable) {
        return new MovieResult<Slice<SearchPageAtMovieMovieDto>>(
                movieQueryService.getSearchPageMovieAtMovieSliced(keyword, pageable));
    }

    @GetMapping("/search/first")
    public MovieSearchResult<Slice<SearchPageAtMovieMovieDto>, RelatedMostTaggedHashtagDto>
    getSearchPageMovieAtMovieSlicedFirst(
            @RequestParam String keyword, @RequestParam int hashtagLength, @RequestParam int length,
            @PageableDefault(sort = "popularity", direction = Sort.Direction.DESC) Pageable pageable)
            throws NotExistMovieException {
        return movieQueryService.getSearchPageMovieAtMovieSlicedFirst(keyword, pageable, length);
    }

    @Data
    @AllArgsConstructor
    public static class MovieResult<T> {
        private T data;
    }

    @Data
    @AllArgsConstructor
    public static class MovieSearchResult<T, F> {
        private SearchPageAtMovieMovieFirstDto firstMovie;
        private T data;
        private F hashtags;
    }

    private List<RelatedMostTaggedHashtagDto> getHashtagsByMovieIds(int length, List<SearchPageAtMovieMovieDto> data) {
        return hashtagQueryService.getHashtagsByPostIds(
                length,
                data.stream().map(SearchPageAtMovieMovieDto::getMovieId).collect(Collectors.toList()));
    }
}
