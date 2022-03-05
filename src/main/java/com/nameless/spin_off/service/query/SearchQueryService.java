package com.nameless.spin_off.service.query;

import com.nameless.spin_off.dto.HashtagDto.MostPopularHashtag;
import com.nameless.spin_off.dto.HashtagDto.RelatedSearchHashtagDto;
import com.nameless.spin_off.dto.MemberDto.RelatedSearchMemberDto;
import com.nameless.spin_off.dto.SearchDto.LastSearchDto;
import com.nameless.spin_off.dto.SearchDto.RelatedSearchAllDto;
import com.nameless.spin_off.dto.SearchDto.SearchAllDto;
import com.nameless.spin_off.dto.SearchDto.SearchFirstDto;
import com.nameless.spin_off.exception.member.NotExistMemberException;
import com.nameless.spin_off.exception.search.OverLengthRelatedKeywordException;
import com.nameless.spin_off.exception.search.UnderLengthRelatedKeywordException;
import org.springframework.data.domain.Pageable;

import java.util.List;

public interface SearchQueryService {

    RelatedSearchAllDto getRelatedSearchAllByKeyword(String keyword, int length)
            throws OverLengthRelatedKeywordException, UnderLengthRelatedKeywordException;
    List<RelatedSearchHashtagDto> getRelatedSearchHashtagByKeyword(String keyword, int length)
            throws OverLengthRelatedKeywordException, UnderLengthRelatedKeywordException;
    List<RelatedSearchMemberDto> getRelatedSearchMemberByKeyword(String keyword, int length)
            throws OverLengthRelatedKeywordException, UnderLengthRelatedKeywordException;
    List<MostPopularHashtag> getMostPopularHashtagLimit(int length);
    List<LastSearchDto> getLastSearchesByMemberLimit(Long memberId, int length) throws NotExistMemberException;

    SearchAllDto getSearchPageDataAtAll(String keyword, Long memberId,
                                        Pageable postPageable,
                                        Pageable collectionPageable,
                                        Pageable memberPageable,
                                        Pageable moviePageable) throws NotExistMemberException;

    SearchFirstDto<SearchAllDto> getSearchPageDataAtAllFirst(String keyword, Long memberId, int length,
                                                    Pageable postPageable,
                                                    Pageable collectionPageable,
                                                    Pageable memberPageable,
                                                    Pageable moviePageable) throws NotExistMemberException;

}
