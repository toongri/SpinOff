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
import com.nameless.spin_off.repository.query.SearchQueryRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Slice;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import static com.nameless.spin_off.entity.enums.search.RelatedSearchEnum.*;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class JpaSearchQueryService implements SearchQueryService {
    private final SearchQueryRepository searchQueryRepository;
    private final MemberRepository memberRepository;

    @Override
    public RelatedSearchAllDto getRelatedSearchAllByKeyword(String keyword)
            throws OverLengthRelatedKeywordException, UnderLengthRelatedKeywordException {

        if (keyword.length() < RELATED_SEARCH_KEYWORD_MIN_STR.getValue()) {
            throw new UnderLengthRelatedKeywordException();
        } else if (keyword.length() > RELATED_SEARCH_KEYWORD_MAX_STR.getValue()) {
            throw new OverLengthRelatedKeywordException();
        } else {
            return getRelatedSearchDtoByKeyword(keyword);
        }
    }

    @Override
    public List<RelatedSearchHashtagDto> getRelatedSearchHashtagByKeyword(String keyword) throws OverLengthRelatedKeywordException, UnderLengthRelatedKeywordException {
        return searchQueryRepository.findRelatedHashtagsAboutKeyword(keyword, RELATED_SEARCH_HASHTAG_NUMBER.getValue());
    }

    @Override
    public List<RelatedSearchMemberDto> getRelatedSearchMemberByKeyword(String keyword) throws OverLengthRelatedKeywordException, UnderLengthRelatedKeywordException {
        return searchQueryRepository.findRelatedMembersAboutKeyword(keyword, RELATED_SEARCH_MEMBER_NUMBER.getValue());
    }

    @Override
    public List<MostPopularHashtag> getMostPopularHashtag() {
        return searchQueryRepository.findMostPopularHashtags();
    }

    @Override
    public List<LastSearchDto> getLastSearchesByMember(Long memberId) {
        return searchQueryRepository.findLastSearchesByMemberId(memberId);
    }

    @Override
    public Slice<SearchPageAtAllMemberDto> getSearchPageMemberAtAllSliced(String keyword, Pageable pageable) {
        return searchQueryRepository.findSearchPageMemberAtAllSliced(keyword, pageable);
    }

    @Override
    public Slice<SearchPageAtAllMovieDto> getSearchPageMovieAtAllSliced(String keyword, Pageable pageable) {
        return searchQueryRepository.findSearchPageMovieAtAllSliced(keyword, pageable);
    }

    @Override
    public Slice<SearchPageAtAllCollectionDto> getSearchPageCollectionAtAllSliced(
            String keyword, Pageable pageable, Long memberId) throws NotExistMemberException {
        List<Member> followedMembers = getMembersByFollowedMemberId(memberId);

        return searchQueryRepository.findSearchPageCollectionAtAllSliced(keyword, pageable, followedMembers);
    }

    @Override
    public Slice<SearchPageAtAllPostDto> getSearchPagePostAtAllSliced(String keyword, Pageable pageable) {
        return searchQueryRepository.findSearchPagePostAtAllSliced(keyword, pageable);
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

    private RelatedSearchAllDto getRelatedSearchDtoByKeyword(String keyword) {
        return new RelatedSearchAllDto(
                searchQueryRepository.findRelatedPostsAboutKeyword(keyword, RELATED_SEARCH_ALL_NUMBER.getValue()),
                searchQueryRepository.findRelatedMoviesAboutKeyword(keyword, RELATED_SEARCH_ALL_NUMBER.getValue()),
                searchQueryRepository.findRelatedHashtagsAboutKeyword(keyword, RELATED_SEARCH_ALL_NUMBER.getValue()),
                searchQueryRepository.findRelatedMembersAboutKeyword(keyword, RELATED_SEARCH_ALL_NUMBER.getValue()),
                searchQueryRepository.findRelatedCollectionsAboutKeyword(keyword, RELATED_SEARCH_ALL_NUMBER.getValue()));
    }
}
