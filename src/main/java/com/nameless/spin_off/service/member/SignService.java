package com.nameless.spin_off.service.member;

import com.nameless.spin_off.dto.MemberDto;
import com.nameless.spin_off.dto.MemberDto.MemberLoginResponseDto;
import com.nameless.spin_off.dto.MemberDto.SocialLoginResponseDto;
import com.nameless.spin_off.dto.MemberDto.TokenResponseDto;
import com.nameless.spin_off.exception.member.AlreadyAccountIdException;
import com.nameless.spin_off.exception.sign.AlreadyNicknameException;

public interface SignService {

    MemberLoginResponseDto registerMember(MemberDto.MemberRegisterRequestDto requestDto)
            throws AlreadyAccountIdException, AlreadyNicknameException;
    MemberLoginResponseDto loginMember(MemberDto.MemberLoginRequestDto requestDto);
    SocialLoginResponseDto loginBySocial(String authCode, String provider);
    TokenResponseDto reIssue(MemberDto.TokenRequestDto requestDto);
    boolean confirmEmail(MemberDto.EmailAuthRequestDto requestDto);
    void checkEmailLinkage(MemberDto.EmailLinkageCheckRequestDto requestDto);
    void updateEmailLinkage(String email, String accountId, String provider);

    boolean sendEmailForAuth(String email) throws AlreadyAccountIdException, AlreadyNicknameException;
    boolean sendEmailForAccountId(String email);
    boolean sendEmailForAccountPw(String accountId);
    boolean checkDuplicateNickname(String email);
    boolean checkDuplicateAccountId(String email);
}
