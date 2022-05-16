package com.nameless.spin_off.service.query;

import com.nameless.spin_off.dto.HashtagDto.MostPopularHashtag;
import com.nameless.spin_off.dto.HashtagDto.RelatedSearchHashtagDto;
import com.nameless.spin_off.dto.MemberDto.RelatedSearchMemberDto;
import com.nameless.spin_off.dto.SearchDto.RelatedSearchAllDto;
import com.nameless.spin_off.dto.SearchDto.SearchAllDto;
import com.nameless.spin_off.dto.SearchDto.SearchAllFirstDto;
import com.nameless.spin_off.dto.SearchDto.SearchFirstDto;
import com.nameless.spin_off.exception.member.NotExistMemberException;
import com.nameless.spin_off.exception.search.IncorrectLengthRelatedKeywordException;
import org.springframework.data.domain.Pageable;

import java.util.List;

public interface SearchQueryService {

    RelatedSearchAllDto getRelatedSearchAllByKeyword(String keyword, int length)
            throws IncorrectLengthRelatedKeywordException;
    List<RelatedSearchHashtagDto> getRelatedSearchHashtagByKeyword(String keyword, int length)
            throws IncorrectLengthRelatedKeywordException;
    List<RelatedSearchMemberDto> getRelatedSearchMemberByKeyword(String keyword, int length)
            throws IncorrectLengthRelatedKeywordException;
    List<MostPopularHashtag> getMostPopularHashtagLimit(int length);

    SearchAllDto getSearchPageDataAtAll(String keyword, Long memberId,
                                                  Pageable postPageable,
                                                  Pageable memberPageable,
                                                  Pageable moviePageable) throws NotExistMemberException;

    SearchFirstDto<SearchAllFirstDto> getSearchPageDataAtAllFirst(String keyword, Long memberId, int length,
                                                                  Pageable postPageable,
                                                                  Pageable collectionPageable,
                                                                  Pageable memberPageable,
                                                                  Pageable moviePageable) throws NotExistMemberException;

}
