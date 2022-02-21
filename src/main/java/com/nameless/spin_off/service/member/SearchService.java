package com.nameless.spin_off.service.member;

import com.nameless.spin_off.dto.HashtagDto.MostPopularHashtag;
import com.nameless.spin_off.dto.SearchDto.LastSearchDto;
import com.nameless.spin_off.dto.SearchDto.RelatedSearchDto;
import com.nameless.spin_off.entity.member.SearchedByMemberStatus;
import com.nameless.spin_off.exception.member.NotExistMemberException;

import java.util.List;

public interface SearchService {

    Long insertSearch(Long memberId, String content, SearchedByMemberStatus searchedByMemberStatus) throws NotExistMemberException;
    List<LastSearchDto> getLastSearchesByMember(Long memberId) throws NotExistMemberException;
    RelatedSearchDto getRelatedSearchByKeyword(String keyword);

    List<MostPopularHashtag> getMostPopularHashtag();

}
