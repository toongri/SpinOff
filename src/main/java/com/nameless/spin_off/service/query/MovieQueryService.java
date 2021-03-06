package com.nameless.spin_off.service.query;

import com.nameless.spin_off.dto.MemberDto.MembersByContentDto;
import com.nameless.spin_off.dto.MovieDto.ReadMovieDto;
import com.nameless.spin_off.dto.MovieDto.SearchAllMovieDto;
import com.nameless.spin_off.dto.MovieDto.SearchMovieDto;
import com.nameless.spin_off.dto.MovieDto.SearchMovieFirstDto;
import com.nameless.spin_off.dto.PostDto.RelatedPostDto;
import com.nameless.spin_off.dto.SearchDto.SearchFirstDto;
import com.nameless.spin_off.exception.movie.NotExistMovieException;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Slice;

import java.util.List;

public interface MovieQueryService {
    Slice<SearchAllMovieDto> getSearchPageMovieAtAllSliced(String keyword, Pageable pageable);
    Slice<SearchMovieDto> getSearchPageMovieAtMovieSliced(String keyword, Pageable pageable);
    SearchFirstDto<SearchMovieFirstDto> getSearchPageMovieAtMovieSlicedFirst(
            String keyword, Pageable pageable, int length) throws NotExistMovieException;
    List<MembersByContentDto> getFollowMovieMembers(Long memberId, Long movieId);
    ReadMovieDto getMovieForRead(Long memberId, Long movieId);
    Slice<RelatedPostDto> getRelatedPostsByMovieIdSliced(Long memberId, Long movieId, Pageable pageable);
}
