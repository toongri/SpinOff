package com.nameless.spin_off.service.query;

import com.nameless.spin_off.dto.MemberDto.SearchPageAtAllMemberDto;
import com.nameless.spin_off.dto.MemberDto.SearchPageAtMemberMemberDto;
import com.nameless.spin_off.exception.member.NotExistMemberException;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Slice;

public interface MemberQueryService {
    Slice<SearchPageAtMemberMemberDto> getSearchPageMemberAtMemberSliced(
            String keyword, Pageable pageable, Long memberId) throws NotExistMemberException;
    Slice<SearchPageAtAllMemberDto> getSearchPageMemberAtAllSliced(String keyword, Pageable pageable, Long memberId) throws NotExistMemberException;
}
