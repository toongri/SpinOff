package com.nameless.spin_off.service.query;

import com.nameless.spin_off.dto.HashtagDto.MostPopularHashtag;
import com.nameless.spin_off.dto.HashtagDto.RelatedSearchHashtagDto;
import com.nameless.spin_off.dto.MemberDto.RelatedSearchMemberDto;
import com.nameless.spin_off.dto.SearchDto;
import com.nameless.spin_off.dto.SearchDto.RelatedSearchAllDto;
import com.nameless.spin_off.exception.member.NotExistMemberException;
import com.nameless.spin_off.exception.search.OverLengthRelatedKeywordException;
import com.nameless.spin_off.exception.search.UnderLengthRelatedKeywordException;

import java.util.List;

public interface SearchQueryService {

    RelatedSearchAllDto getRelatedSearchAllByKeyword(String keyword) throws OverLengthRelatedKeywordException, UnderLengthRelatedKeywordException;
    List<RelatedSearchHashtagDto> getRelatedSearchHashtagByKeyword(String keyword) throws OverLengthRelatedKeywordException, UnderLengthRelatedKeywordException;
    List<RelatedSearchMemberDto> getRelatedSearchMemberByKeyword(String keyword) throws OverLengthRelatedKeywordException, UnderLengthRelatedKeywordException;
    List<MostPopularHashtag> getMostPopularHashtag();
    List<SearchDto.LastSearchDto> getLastSearchesByMember(Long memberId) throws NotExistMemberException;

}
