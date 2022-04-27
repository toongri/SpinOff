package com.nameless.spin_off.service.query;

import com.nameless.spin_off.config.member.MemberDetails;
import com.nameless.spin_off.dto.CollectionDto.FollowCollectionDto;
import com.nameless.spin_off.dto.HashtagDto.FollowHashtagDto;
import com.nameless.spin_off.dto.MemberDto.MembersByContentDto;
import com.nameless.spin_off.dto.MemberDto.ReadMemberDto;
import com.nameless.spin_off.dto.MemberDto.SearchAllMemberDto;
import com.nameless.spin_off.dto.MemberDto.SearchMemberDto;
import com.nameless.spin_off.dto.MovieDto.FollowMovieDto;
import com.nameless.spin_off.dto.SearchDto.LastSearchDto;
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
    List<LastSearchDto> getLastSearchesByMemberLimit(Long memberId, int length) throws NotExistMemberException;
    ReadMemberDto getMemberForRead(MemberDetails currentMember, Long targetMemberId);
    List<MembersByContentDto> getFollowedMembersByMemberId(Long currentMemberId, Long targetMemberId);
    List<MembersByContentDto> getFollowingMembersByMemberId(Long currentMemberId, Long targetMemberId);
    List<FollowHashtagDto> getFollowHashtagsByMemberId(Long currentMemberId, Long targetMemberId);
    List<FollowMovieDto> getFollowMoviesByMemberId(Long currentMemberId, Long targetMemberId);
    List<FollowCollectionDto> getFollowCollectionsByMemberId(MemberDetails currentMember, Long targetMemberId);
}
