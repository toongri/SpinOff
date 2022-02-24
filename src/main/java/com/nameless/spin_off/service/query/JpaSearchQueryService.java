package com.nameless.spin_off.service.query;

import com.nameless.spin_off.dto.HashtagDto.MostPopularHashtag;
import com.nameless.spin_off.dto.HashtagDto.RelatedSearchHashtagDto;
import com.nameless.spin_off.dto.MemberDto.RelatedSearchMemberDto;
import com.nameless.spin_off.dto.SearchDto.LastSearchDto;
import com.nameless.spin_off.dto.SearchDto.RelatedSearchAllDto;
import com.nameless.spin_off.exception.search.OverLengthRelatedKeywordException;
import com.nameless.spin_off.exception.search.UnderLengthRelatedKeywordException;
import com.nameless.spin_off.repository.query.SearchQueryRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

import static com.nameless.spin_off.StaticVariable.*;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class JpaSearchQueryService implements SearchQueryService {
    private final SearchQueryRepository searchQueryRepository;


    @Override
    public RelatedSearchAllDto getRelatedSearchAllByKeyword(String keyword)
            throws OverLengthRelatedKeywordException, UnderLengthRelatedKeywordException {

        if (keyword.length() < RELATED_SEARCH_KEYWORD_MIN_STR) {
            throw new UnderLengthRelatedKeywordException();
        } else if (keyword.length() > RELATED_SEARCH_KEYWORD_MAX_STR) {
            throw new OverLengthRelatedKeywordException();
        } else {
            return getRelatedSearchDtoByKeyword(keyword);
        }
    }

    @Override
    public List<RelatedSearchHashtagDto> getRelatedSearchHashtagByKeyword(String keyword) throws OverLengthRelatedKeywordException, UnderLengthRelatedKeywordException {
        return searchQueryRepository.getRelatedHashtagsAboutKeyword(keyword, RELATED_SEARCH_HASHTAG_NUMBER);
    }

    @Override
    public List<RelatedSearchMemberDto> getRelatedSearchMemberByKeyword(String keyword) throws OverLengthRelatedKeywordException, UnderLengthRelatedKeywordException {
        return searchQueryRepository.getRelatedMembersAboutKeyword(keyword, RELATED_SEARCH_MEMBER_NUMBER);
    }

    @Override
    public List<MostPopularHashtag> getMostPopularHashtag() {
        return searchQueryRepository.getMostPopularHashtags();
    }

    @Override
    public List<LastSearchDto> getLastSearchesByMember(Long memberId) {
        return searchQueryRepository.getLastSearchesByMemberId(memberId);
    }

    private RelatedSearchAllDto getRelatedSearchDtoByKeyword(String keyword) {
        return new RelatedSearchAllDto(
                searchQueryRepository.getRelatedPostsAboutKeyword(keyword, RELATED_SEARCH_ALL_NUMBER),
                searchQueryRepository.getRelatedMoviesAboutKeyword(keyword, RELATED_SEARCH_ALL_NUMBER),
                searchQueryRepository.getRelatedHashtagsAboutKeyword(keyword, RELATED_SEARCH_ALL_NUMBER),
                searchQueryRepository.getRelatedMembersAboutKeyword(keyword, RELATED_SEARCH_ALL_NUMBER),
                searchQueryRepository.getRelatedCollectionsAboutKeyword(keyword, RELATED_SEARCH_ALL_NUMBER));
    }
}
