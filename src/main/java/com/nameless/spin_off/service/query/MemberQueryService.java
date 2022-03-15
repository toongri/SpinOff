package com.nameless.spin_off.service.query;

import com.nameless.spin_off.dto.MemberDto.SearchAllMemberDto;
import com.nameless.spin_off.dto.MemberDto.SearchMemberDto;
import com.nameless.spin_off.dto.SearchDto;
import com.nameless.spin_off.dto.SearchDto.SearchFirstDto;
import com.nameless.spin_off.exception.member.NotExistMemberException;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Slice;

import java.util.List;

public interface MemberQueryService {
    Slice<SearchMemberDto> getSearchPageMemberAtMemberSliced(
            String keyword, Pageable pageable, Long memberId) throws NotExistMemberException;
    Slice<SearchAllMemberDto> getSearchPageMemberAtAllSliced(String keyword, Pageable pageable, Long memberId)
            throws NotExistMemberException;

    SearchFirstDto<Slice<SearchMemberDto>> getSearchPageMemberAtMemberSlicedFirst(
            String keyword, Pageable pageable, Long memberId, int length) throws NotExistMemberException;

    List<SearchDto.LastSearchDto> getLastSearchesByMemberLimit(Long memberId, int length) throws NotExistMemberException;
}
