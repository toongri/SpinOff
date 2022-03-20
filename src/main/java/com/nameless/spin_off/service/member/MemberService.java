package com.nameless.spin_off.service.member;

import com.nameless.spin_off.dto.MemberDto.*;
import com.nameless.spin_off.entity.enums.member.BlockedMemberStatus;
import com.nameless.spin_off.entity.enums.member.SearchedByMemberStatus;
import com.nameless.spin_off.exception.member.*;

public interface MemberService {

    Long insertMemberByMemberVO(MemberRegisterRequestDto requestDto)
            throws AlreadyAccountIdException, AlreadyNicknameException;

    MemberRegisterResponseDto registerMember(MemberRegisterRequestDto requestDto)
            throws AlreadyAccountIdException, AlreadyNicknameException;
    MemberLoginResponseDto loginMember(MemberLoginRequestDto requestDto);
    TokenResponseDto reIssue(TokenRequestDto requestDto);
    boolean confirmEmail(EmailAuthRequestDto requestDto);
    void checkEmailLinkage(EmailLinkageCheckRequestDto requestDto);
    void updateEmailLinkage(String email, String accountId);
    int updateAllPopularity();
    Long insertFollowedMemberByMemberId(Long memberId, Long followedMemberId)
            throws NotExistMemberException, AlreadyFollowedMemberException;
    Long insertBlockedMemberByMemberId(Long memberId, Long blockedMemberId, BlockedMemberStatus blockedMemberStatus)
            throws NotExistMemberException, AlreadyBlockedMemberException, AlreadyFollowedMemberException;
    Long insertSearch(Long memberId, String content, SearchedByMemberStatus searchedByMemberStatus)
            throws NotExistMemberException;
    boolean sendEmailAuth(String email)
            throws AlreadyAccountIdException, AlreadyNicknameException;
    boolean checkDuplicateNickname(String email);
    boolean checkDuplicateAccountId(String email);
}
