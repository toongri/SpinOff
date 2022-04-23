package com.nameless.spin_off.service.query;

import com.nameless.spin_off.dto.HashtagDto.RelatedMostTaggedHashtagDto;
import com.nameless.spin_off.dto.MemberDto.MembersByContentDto;
import com.nameless.spin_off.dto.MovieDto.SearchAllMovieDto;
import com.nameless.spin_off.dto.MovieDto.SearchMovieAboutFirstMovieDto;
import com.nameless.spin_off.dto.MovieDto.SearchMovieDto;
import com.nameless.spin_off.dto.MovieDto.SearchMovieFirstDto;
import com.nameless.spin_off.dto.SearchDto.SearchFirstDto;
import com.nameless.spin_off.entity.enums.ErrorEnum;
import com.nameless.spin_off.entity.enums.member.BlockedMemberStatus;
import com.nameless.spin_off.entity.movie.Movie;
import com.nameless.spin_off.exception.movie.NotExistMovieException;
import com.nameless.spin_off.repository.member.MemberRepository;
import com.nameless.spin_off.repository.movie.MovieRepository;
import com.nameless.spin_off.repository.query.HashtagQueryRepository;
import com.nameless.spin_off.repository.query.MovieQueryRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Slice;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Collections;
import java.util.Comparator;
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
    private final MemberRepository memberRepository;
    private List<Long> blockedMemberIds;

    @Override
    public Slice<SearchAllMovieDto> getSearchPageMovieAtAllSliced(String keyword, Pageable pageable) {
        return movieQueryRepository.findAllSlicedForSearchPageAtAll(keyword, pageable);
    }

    @Override
    public Slice<SearchMovieDto> getSearchPageMovieAtMovieSliced(String keyword, Pageable pageable) {
        return movieQueryRepository.findAllSlicedForSearchPageAtMovie(keyword, pageable);
    }

    @Override
    public SearchFirstDto<SearchMovieFirstDto> getSearchPageMovieAtMovieSlicedFirst(String keyword, Pageable pageable, int length)
            throws NotExistMovieException {

        Slice<SearchMovieDto> movies =
                movieQueryRepository.findAllSlicedForSearchPageAtMovie(keyword, pageable);

        SearchMovieAboutFirstMovieDto first = getMovieForSearchPageFirst(movies);

        List<RelatedMostTaggedHashtagDto> hashtags = hashtagQueryRepository.findAllByMovieIds(
                length, movies.getContent().stream()
                        .map(SearchMovieDto::getMovieId)
                        .collect(Collectors.toList()));

        return new SearchFirstDto<>(new SearchMovieFirstDto(first, movies), hashtags);
    }

    @Override
    public List<MembersByContentDto> getFollowMovieMembers(Long memberId, Long movieId) {
        blockedMemberIds = getBlockingAllAndBlockedAllByIdAndBlockStatusA(memberId);
        return getMembersByContentDtos(getFollowMembersByMovieId(movieId), memberId);
    }

    private SearchMovieAboutFirstMovieDto getMovieForSearchPageFirst(Slice<SearchMovieDto> movies)
            throws NotExistMovieException {

        if (!movies.isEmpty()) {
            Optional<Movie> optionalMovie =
                    movieRepository.findOneByIdWithTaggedPost(movies.getContent().get(0).getMovieId());

            Movie movie = optionalMovie.orElseThrow(() -> new NotExistMovieException(ErrorEnum.NOT_EXIST_MOVIE));

            return new SearchMovieAboutFirstMovieDto(movie);

        } else {
            return null;
        }
    }

    private List<MembersByContentDto> getMembersByContentDtos(List<MembersByContentDto> members, Long memberId) {
        List<Long> followingMemberIds = getAllIdByFollowingMemberId(memberId);
        members.forEach(m -> m.setFollowedAndOwn(followingMemberIds.contains(m.getMemberId()), memberId));
        return members.stream()
                .sorted(
                        Comparator.comparing(MembersByContentDto::isOwn)
                                .thenComparing(MembersByContentDto::isFollowed).reversed())
                .collect(Collectors.toList());
    }

    private List<Long> getBlockingAllAndBlockedAllByIdAndBlockStatusA(Long memberId) {
        if (memberId != null) {
            return memberRepository.findBlockingAllAndBlockedAllByIdAndBlockStatus(memberId, BlockedMemberStatus.A);
        } else{
            return Collections.emptyList();
        }
    }

    private List<MembersByContentDto> getFollowMembersByMovieId(Long movieId) {
        return movieQueryRepository.findAllFollowMemberByMovieId(movieId, blockedMemberIds);
    }

    private List<Long> getAllIdByFollowingMemberId(Long memberId) {
        if (memberId != null) {
            return memberRepository.findAllIdByFollowingMemberId(memberId);
        } else {
            return Collections.emptyList();
        }
    }
}
