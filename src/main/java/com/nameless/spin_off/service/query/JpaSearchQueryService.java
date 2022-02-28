package com.nameless.spin_off.service.query;

import com.nameless.spin_off.dto.CollectionDto.SearchPageAtAllCollectionDto;
import com.nameless.spin_off.dto.HashtagDto.MostPopularHashtag;
import com.nameless.spin_off.dto.HashtagDto.RelatedSearchHashtagDto;
import com.nameless.spin_off.dto.MemberDto.RelatedSearchMemberDto;
import com.nameless.spin_off.dto.MemberDto.SearchPageAtAllMemberDto;
import com.nameless.spin_off.dto.MovieDto.SearchPageAtAllMovieDto;
import com.nameless.spin_off.dto.PostDto.SearchPageAtAllPostDto;
import com.nameless.spin_off.dto.SearchDto.LastSearchDto;
import com.nameless.spin_off.dto.SearchDto.RelatedSearchAllDto;
import com.nameless.spin_off.entity.member.FollowedMember;
import com.nameless.spin_off.entity.member.Member;
import com.nameless.spin_off.exception.member.NotExistMemberException;
import com.nameless.spin_off.exception.search.OverLengthRelatedKeywordException;
import com.nameless.spin_off.exception.search.UnderLengthRelatedKeywordException;
import com.nameless.spin_off.repository.member.MemberRepository;
import com.nameless.spin_off.repository.query.*;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Slice;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import static com.nameless.spin_off.entity.enums.search.RelatedSearchEnum.RELATED_SEARCH_KEYWORD_MAX_STR;
import static com.nameless.spin_off.entity.enums.search.RelatedSearchEnum.RELATED_SEARCH_KEYWORD_MIN_STR;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class JpaSearchQueryService implements SearchQueryService {
    private final QuerydslSearchQueryRepository searchQueryRepository;
    private final MemberQueryRepository memberQueryRepository;
    private final CollectionQueryRepository collectionQueryRepository;
    private final MovieQueryRepository movieQueryRepository;
    private final PostQueryRepository postQueryRepository;
    private final MemberRepository memberRepository;

    @Override
    public RelatedSearchAllDto getRelatedSearchAllByKeyword(String keyword, int length)
            throws OverLengthRelatedKeywordException, UnderLengthRelatedKeywordException {

        if (keyword.length() < RELATED_SEARCH_KEYWORD_MIN_STR.getValue()) {
            throw new UnderLengthRelatedKeywordException();
        } else if (keyword.length() > RELATED_SEARCH_KEYWORD_MAX_STR.getValue()) {
            throw new OverLengthRelatedKeywordException();
        } else {
            return getRelatedSearchDtoByKeyword(keyword, length);
        }
    }

    @Override
    public List<RelatedSearchHashtagDto> getRelatedSearchHashtagByKeyword(String keyword, int length)
            throws OverLengthRelatedKeywordException, UnderLengthRelatedKeywordException {
        return searchQueryRepository.findRelatedHashtagsAboutKeyword(keyword, length);
    }

    @Override
    public List<RelatedSearchMemberDto> getRelatedSearchMemberByKeyword(String keyword, int length)
            throws OverLengthRelatedKeywordException, UnderLengthRelatedKeywordException {
        return searchQueryRepository.findRelatedMembersAboutKeyword(keyword, length);
    }

    @Override
    public List<MostPopularHashtag> getMostPopularHashtagLimit(int length) {
        return searchQueryRepository.findMostPopularHashtagsLimit(length);
    }

    @Override
    public List<LastSearchDto> getLastSearchesByMemberLimit(Long memberId, int length) {
        return searchQueryRepository.findLastSearchesByMemberIdLimit(memberId, length);
    }

    @Override
    public Slice<SearchPageAtAllMemberDto> getSearchPageMemberAtAllSliced(String keyword, Pageable pageable) {
        return memberQueryRepository.findAllSlicedSearchPageAtAll(keyword, pageable);
    }

    @Override
    public Slice<SearchPageAtAllMovieDto> getSearchPageMovieAtAllSliced(String keyword, Pageable pageable) {
        return movieQueryRepository.findAllSlicedForSearchPageAtAll(keyword, pageable);
    }

    @Override
    public Slice<SearchPageAtAllCollectionDto> getSearchPageCollectionAtAllSliced(
            String keyword, Pageable pageable, Long memberId) throws NotExistMemberException {
        List<Member> followedMembers = getMembersByFollowedMemberId(memberId);

        return collectionQueryRepository.findAllSlicedSearchPageAtAll(keyword, pageable, followedMembers);
    }

    @Override
    public Slice<SearchPageAtAllPostDto> getSearchPagePostAtAllSliced(String keyword, Pageable pageable) {
        return postQueryRepository.findAllSlicedSearchPageAtAll(keyword, pageable);
    }

    private List<Member> getMembersByFollowedMemberId(Long memberId) throws NotExistMemberException {
        if (memberId == null) {
            return List.of();
        } else {
            Optional<Member> optionalMember = memberRepository.findOneByIdWithFollowedMember(memberId);
            return optionalMember.orElseThrow(NotExistMemberException::new)
                    .getFollowedMembers().stream().map(FollowedMember::getMember).collect(Collectors.toList());
        }
    }

    private RelatedSearchAllDto getRelatedSearchDtoByKeyword(String keyword, int length) {
        return new RelatedSearchAllDto(
                searchQueryRepository.findRelatedPostsAboutKeyword(keyword, length),
                searchQueryRepository.findRelatedMoviesAboutKeyword(keyword, length),
                searchQueryRepository.findRelatedHashtagsAboutKeyword(keyword, length),
                searchQueryRepository.findRelatedMembersAboutKeyword(keyword, length),
                searchQueryRepository.findRelatedCollectionsAboutKeyword(keyword, length));
    }
}
