package com.nameless.spin_off.service.member;

import com.nameless.spin_off.controller.api.SearchApiController;
import com.nameless.spin_off.controller.api.SearchApiController.SearchApiResult;
import com.nameless.spin_off.dto.MemberDto;
import com.nameless.spin_off.dto.MemberDto.RelatedSearchMemberDto;
import com.nameless.spin_off.dto.SearchDto;
import com.nameless.spin_off.dto.SearchDto.RelatedSearchDto;
import com.nameless.spin_off.entity.member.SearchedByMember;
import com.nameless.spin_off.entity.member.SearchedByMemberStatus;
import com.nameless.spin_off.exception.member.NotExistMemberException;

import java.util.List;

public interface SearchService {

    Long insertSearch(Long memberId, String content, SearchedByMemberStatus searchedByMemberStatus) throws NotExistMemberException;
    List<SearchedByMember> getLastSearchesByMember(Long memberId) throws NotExistMemberException;
    RelatedSearchDto getRelatedSearchByKeyword(String keyword);
}
