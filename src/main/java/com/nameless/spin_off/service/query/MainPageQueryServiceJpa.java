package com.nameless.spin_off.service.query;

import com.nameless.spin_off.dto.CollectionDto.MainPageCollectionDto;
import com.nameless.spin_off.dto.MainPageDto.MainPageDiscoveryDto;
import com.nameless.spin_off.dto.MainPageDto.MainPageFollowDto;
import com.nameless.spin_off.enums.member.BlockedMemberStatus;
import com.nameless.spin_off.exception.member.NotExistMemberException;
import com.nameless.spin_off.repository.member.MemberRepository;
import com.nameless.spin_off.repository.movie.MovieRepository;
import com.nameless.spin_off.repository.query.CollectionQueryRepository;
import com.nameless.spin_off.repository.query.PostQueryRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Slice;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Collections;
import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class MainPageQueryServiceJpa implements MainPageQueryService {

    private final PostQueryRepository postQueryRepository;
    private final CollectionQueryRepository collectionQueryRepository;
    private final MemberRepository memberRepository;
    private final MovieRepository movieRepository;
    private List<Long> blockedMemberIds;

    @Override
    public MainPageDiscoveryDto getDiscoveryData(Pageable popularPostPageable, Pageable latestPostPageable,
                                                 Pageable collectionPageable, Long memberId)
            throws NotExistMemberException {

        blockedMemberIds = getBlockingAllAndBlockedAllByIdAndBlockStatusA(memberId);

        return new MainPageDiscoveryDto(
                postQueryRepository.findAllSlicedForMainPage(popularPostPageable, memberId, blockedMemberIds),
                postQueryRepository.findAllSlicedForMainPage(latestPostPageable, memberId, blockedMemberIds),
                collectionQueryRepository.findAllSlicedForMainPage(collectionPageable, memberId, blockedMemberIds));
    }

    @Override
    public MainPageFollowDto getFollowData(
            Pageable memberPageable, Pageable hashtagPageable,
            Pageable moviePageable, Pageable collectionPageable, Long memberId)
            throws NotExistMemberException {

        List<Long> followedMemberIds = getFollowedMemberByMemberId(memberId);

        blockedMemberIds = getBlockingAllAndBlockedAllByIdAndBlockStatusA(memberId);

        return new MainPageFollowDto(
                postQueryRepository.findAllByFollowingMemberSlicedForMainPage(memberPageable, memberId),
                postQueryRepository.findAllByFollowedHashtagsSlicedForMainPage(hashtagPageable,
                        getFollowedMovieByMemberId(memberId), followedMemberIds, blockedMemberIds, memberId),
                postQueryRepository.findAllByFollowedMoviesSlicedForMainPage(moviePageable,
                        followedMemberIds, blockedMemberIds, memberId),
                getMainPageCollectionList(collectionPageable, memberId));
    }

    private Slice<MainPageCollectionDto> getMainPageCollectionList(Pageable pageable, Long memberId) {
        if (pageable.getPageNumber() % 2 == 0) {
            return collectionQueryRepository.findAllByFollowedMemberSlicedForMainPage(
                    PageRequest.of(pageable.getPageNumber() / 2, pageable.getPageSize(),
                            pageable.getSort()), memberId);
        } else {
             return collectionQueryRepository.findAllByFollowedCollectionsSlicedForMainPage(
                    PageRequest.of(pageable.getPageNumber() / 2, pageable.getPageSize(),
                            pageable.getSort()), memberId);
        }
    }

    private List<Long> getBlockingAllAndBlockedAllByIdAndBlockStatusA(Long memberId) {
        if (memberId != null) {
            return memberRepository.findBlockingAllAndBlockedAllByIdAndBlockStatus(memberId, BlockedMemberStatus.A);
        } else{
            return Collections.emptyList();
        }
    }

    private List<Long> getFollowedMemberByMemberId(Long memberId) {
        if (memberId != null) {
            return memberRepository.findAllIdByFollowingMemberId(memberId);
        } else{
            return Collections.emptyList();
        }
    }

    private List<Long> getFollowedMovieByMemberId(Long memberId) {
        if (memberId != null) {
            return movieRepository.findAllIdByFollowingMemberId(memberId);
        } else{
            return Collections.emptyList();
        }
    }
}
