package com.nameless.spin_off.service.member;

import com.nameless.spin_off.config.member.MemberDetails;
import com.nameless.spin_off.dto.MemberDto.EmailAuthRequestDto;
import com.nameless.spin_off.dto.MemberDto.MemberInfoRequestDto;
import com.nameless.spin_off.dto.MemberDto.MemberProfileRequestDto;
import com.nameless.spin_off.enums.member.BlockedMemberStatus;
import com.nameless.spin_off.enums.member.SearchedByMemberStatus;
import com.nameless.spin_off.exception.member.AlreadyBlockedMemberException;
import com.nameless.spin_off.exception.member.AlreadyFollowedMemberException;
import com.nameless.spin_off.exception.member.NotExistMemberException;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.time.LocalDate;

public interface MemberService {
    Long insertFollowedMemberByMemberId(Long memberId, Long followedMemberId)
            throws NotExistMemberException, AlreadyFollowedMemberException;
    Long insertBlockedMemberByMemberId(Long memberId, Long blockedMemberId, BlockedMemberStatus blockedMemberStatus)
            throws NotExistMemberException, AlreadyBlockedMemberException, AlreadyFollowedMemberException;
    Long insertSearch(Long memberId, String content, SearchedByMemberStatus searchedByMemberStatus)
            throws NotExistMemberException;
    Long updateMemberProfile(Long memberId, MemberProfileRequestDto memberProfileDto, MultipartFile multipartFile) throws IOException;
    Long updateMemberInfo(Long memberId, MemberInfoRequestDto memberInfoRequestDto);
    Boolean isMatchedPassword(MemberDetails currentMember, String password);
    Boolean updateMemberPassword(Long memberId, String password);
    boolean sendEmailForAuth(Long memberId, String email);
    boolean confirmEmailForAuth(EmailAuthRequestDto requestDto);
    boolean sendEmailForUpdateEmail(Long memberId, String email);
    boolean confirmEmailForUpdateEMail(EmailAuthRequestDto requestDto);
    boolean updateMemberDeleteDate(Long memberId, LocalDate localDate);
    boolean updateMemberDeleteDateToNull(Long memberId);
    int updateAllPopularity();
}
