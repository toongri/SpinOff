package com.nameless.spin_off.service.query;

import com.nameless.spin_off.dto.HashtagDto.MostPopularHashtag;
import com.nameless.spin_off.dto.HashtagDto.RelatedMostTaggedHashtagDto;
import com.nameless.spin_off.dto.HashtagDto.RelatedSearchHashtagDto;
import com.nameless.spin_off.dto.MemberDto.RelatedSearchMemberDto;
import com.nameless.spin_off.dto.PostDto.SearchPageAtAllPostDto;
import com.nameless.spin_off.dto.SearchDto.RelatedSearchAllDto;
import com.nameless.spin_off.dto.SearchDto.SearchAllDto;
import com.nameless.spin_off.dto.SearchDto.SearchFirstDto;
import com.nameless.spin_off.entity.enums.ErrorEnum;
import com.nameless.spin_off.entity.enums.member.BlockedMemberStatus;
import com.nameless.spin_off.exception.member.NotExistMemberException;
import com.nameless.spin_off.exception.search.IncorrectLengthRelatedKeywordException;
import com.nameless.spin_off.repository.member.MemberRepository;
import com.nameless.spin_off.repository.query.*;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Slice;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import static com.nameless.spin_off.entity.enums.search.SearchEnum.RELATED_SEARCH_KEYWORD_MAX_STR;
import static com.nameless.spin_off.entity.enums.search.SearchEnum.RELATED_SEARCH_KEYWORD_MIN_STR;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class SearchQueryServiceJpa implements SearchQueryService {
    private final SearchQueryRepositoryQuerydsl searchQueryRepository;
    private final MemberQueryRepository memberQueryRepository;
    private final CollectionQueryRepository collectionQueryRepository;
    private final MovieQueryRepository movieQueryRepository;
    private final PostQueryRepository postQueryRepository;
    private final MemberRepository memberRepository;
    private final HashtagQueryRepository hashtagQueryRepository;

    @Override
    public RelatedSearchAllDto getRelatedSearchAllByKeyword(String keyword, int length)
            throws IncorrectLengthRelatedKeywordException {

        if (keyword.length() < RELATED_SEARCH_KEYWORD_MIN_STR.getValue()) {
            throw new IncorrectLengthRelatedKeywordException(ErrorEnum.INCORRECT_LENGTH_RELATED_KEYWORD);
        } else if (keyword.length() > RELATED_SEARCH_KEYWORD_MAX_STR.getValue()) {
            throw new IncorrectLengthRelatedKeywordException(ErrorEnum.INCORRECT_LENGTH_RELATED_KEYWORD);
        } else {
            return getRelatedSearchDtoByKeyword(keyword, length);
        }
    }

    @Override
    public List<RelatedSearchHashtagDto> getRelatedSearchHashtagByKeyword(String keyword, int length)
            throws IncorrectLengthRelatedKeywordException {
        return searchQueryRepository.findRelatedHashtagsAboutKeyword(keyword, length);
    }

    @Override
    public List<RelatedSearchMemberDto> getRelatedSearchMemberByKeyword(String keyword, int length)
            throws IncorrectLengthRelatedKeywordException {
        return searchQueryRepository.findRelatedMembersAboutKeyword(keyword, length);
    }

    @Override
    public List<MostPopularHashtag> getMostPopularHashtagLimit(int length) {
        return searchQueryRepository.findMostPopularHashtagsLimit(length);
    }

    @Override
    public SearchAllDto getSearchPageDataAtAll(
            String keyword, Long memberId, Pageable postPageable, Pageable collectionPageable,
            Pageable memberPageable, Pageable moviePageable) throws NotExistMemberException {

        List<Long> blockedMembers = getBlockingAllAndBlockedAllByIdAndBlockStatusA(memberId);

        return new SearchAllDto(
                postQueryRepository.findAllSlicedForSearchPageAtAll(keyword, postPageable, blockedMembers),
                collectionQueryRepository.findAllSlicedForSearchPageAtAll(
                        keyword, collectionPageable, blockedMembers, memberId),
                movieQueryRepository.findAllSlicedForSearchPageAtAll(keyword, moviePageable),
                memberQueryRepository.findAllSlicedForSearchPageAtAll(keyword, memberPageable, blockedMembers));
    }

    @Override
    public SearchFirstDto<SearchAllDto> getSearchPageDataAtAllFirst(
            String keyword, Long memberId, int length, Pageable postPageable, Pageable collectionPageable,
            Pageable memberPageable, Pageable moviePageable) throws NotExistMemberException {

        List<Long> blockedMembers = getBlockingAllAndBlockedAllByIdAndBlockStatusA(memberId);

        Slice<SearchPageAtAllPostDto> posts = postQueryRepository.findAllSlicedForSearchPageAtAll(
                keyword, postPageable, blockedMembers);

        return new SearchFirstDto<>(
                new SearchAllDto(
                posts,
                collectionQueryRepository.findAllSlicedForSearchPageAtAll(
                        keyword, collectionPageable, blockedMembers, memberId),
                movieQueryRepository.findAllSlicedForSearchPageAtAll(
                        keyword, moviePageable),
                memberQueryRepository.findAllSlicedForSearchPageAtAll(
                        keyword, memberPageable, blockedMembers)),
                getHashtagsByPostIdsAtAll(length, posts.getContent()));
    }

    private RelatedSearchAllDto getRelatedSearchDtoByKeyword(String keyword, int length) {
        return new RelatedSearchAllDto(
                searchQueryRepository.findRelatedPostsAboutKeyword(keyword, length),
                searchQueryRepository.findRelatedMoviesAboutKeyword(keyword, length),
                searchQueryRepository.findRelatedHashtagsAboutKeyword(keyword, length),
                searchQueryRepository.findRelatedMembersAboutKeyword(keyword, length),
                searchQueryRepository.findRelatedCollectionsAboutKeyword(keyword, length));
    }

    private List<RelatedMostTaggedHashtagDto> getHashtagsByPostIdsAtAll(int length, List<SearchPageAtAllPostDto> data) {
        return hashtagQueryRepository.findAllByPostIds(
                length,
                data.stream().map(SearchPageAtAllPostDto::getPostId).collect(Collectors.toList()));
    }

    private List<Long> getBlockingAllAndBlockedAllByIdAndBlockStatusA(Long memberId) {
        if (memberId != null) {
            return memberRepository.findBlockingAllAndBlockedAllByIdAndBlockStatus(memberId, BlockedMemberStatus.A);
        } else{
            return new ArrayList<>();
        }
    }

    private List<Long> getFollowedMemberByMemberId(Long memberId) {
        if (memberId != null) {
            return memberRepository.findAllIdByFollowingMemberId(memberId);
        } else{
            return new ArrayList<>();
        }
    }
}
