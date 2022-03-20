package com.nameless.spin_off.service.member;

import com.nameless.spin_off.config.jwt.JwtTokenProvider;
import com.nameless.spin_off.dto.MemberDto.*;
import com.nameless.spin_off.entity.collection.Collection;
import com.nameless.spin_off.entity.enums.member.*;
import com.nameless.spin_off.entity.member.*;
import com.nameless.spin_off.exception.member.*;
import com.nameless.spin_off.exception.security.InvalidRefreshTokenException;
import com.nameless.spin_off.repository.collection.CollectionRepository;
import com.nameless.spin_off.repository.member.*;
import com.nameless.spin_off.repository.query.EmailAuthQueryRepository;
import com.nameless.spin_off.repository.query.EmailLinkageQueryRepository;
import com.nameless.spin_off.repository.query.MemberQueryRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.RandomStringUtils;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static com.nameless.spin_off.entity.enums.ContentsLengthEnum.EMAIL_TOKEN;
import static com.nameless.spin_off.entity.enums.ContentsTimeEnum.EMAIL_AUTH_MINUTE;
import static com.nameless.spin_off.entity.enums.ContentsTimeEnum.REGISTER_EMAIL_AUTH_MINUTE;
import static com.nameless.spin_off.entity.enums.member.EmailLinkageServiceEnum.*;

@Slf4j
@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class MemberServiceJpa implements MemberService {

    private final MemberRepository memberRepository;
    private final CollectionRepository collectionRepository;
    private final JwtTokenProvider jwtTokenProvider;
    private final PasswordEncoder passwordEncoder;
    private final EmailAuthQueryRepository emailAuthQueryRepository;
    private final EmailLinkageQueryRepository emailLinkageQueryRepository;
    private final EmailAuthRepository emailAuthRepository;
    private final EmailLinkageRepository emailLinkageRepository;
    private final EmailService emailService;
    private final MemberQueryRepository memberQueryRepository;
    private final FollowedMemberRepository followedMemberRepository;
    private final BlockedMemberRepository blockedMemberRepository;

    @Transactional
    @Override
    public Long insertMemberByMemberVO(MemberRegisterRequestDto memberVO)
            throws AlreadyAccountIdException, AlreadyNicknameException {

        validateDuplicateAtRegister(memberVO.getAccountId(), memberVO.getNickname(), memberVO.getEmail());

        Member member = memberRepository.save(Member.createMemberByCreateVO(memberVO));
        collectionRepository.save(Collection.createDefaultCollection(member));
        return member.getId();
    }

    @Transactional
    @Override
    public boolean sendEmailAuth(String email)
            throws AlreadyAccountIdException, AlreadyNicknameException {

        isCorrectEmail(email);
        checkDuplicateEmail(email);

        EmailAuth emailAuth = emailAuthRepository.save(EmailAuth.builder()
                .email(email)
//                .authToken(UUID.randomUUID().toString())
                .authToken(RandomStringUtils.randomAlphabetic(EMAIL_TOKEN.getLength()))
                .expired(false)
                .provider(EmailAuthProviderStatus.A)
                .build());

        emailService.sendForRegister(emailAuth.getEmail(), emailAuth.getAuthToken());
        return true;
    }

    @Transactional
    @Override
    public MemberRegisterResponseDto registerMember(MemberRegisterRequestDto requestDto)
            throws AlreadyAccountIdException, AlreadyNicknameException {
        isCorrectRegisterRequest(requestDto);
        isExistAuthEmail(requestDto.getEmail(), requestDto.getAuthToken());

        Member member = memberRepository.save(Member.buildMember()
                .setNickname(requestDto.getNickname())
                .setEmail(requestDto.getEmail())
                .setAccountId(requestDto.getAccountId())
                .setAccountPw(passwordEncoder.encode(requestDto.getAccountPw()))
                .setBirth(requestDto.getBirth())
                .setName(requestDto.getName())
                .build());

        return MemberRegisterResponseDto.builder()
                .id(member.getId())
                .accountId(member.getAccountId())
                .build();
    }

    @Transactional
    @Override
    public int updateAllPopularity() {
        List<Member> members =
                memberQueryRepository.findAllByViewAfterTime(MemberScoreEnum.MEMBER_FOLLOW.getOldestDate());
        for (Member member : members) {
            member.updatePopularity();
        }
        return members.size();
    }

    @Transactional
    @Override
    public boolean confirmEmail(EmailAuthRequestDto requestDto) {

        EmailAuth emailAuth = emailAuthQueryRepository.findValidAuthByEmail(
                        requestDto.getEmail(),
                        requestDto.getAuthToken(),
                        EMAIL_AUTH_MINUTE.getDateTime(),
                        EmailAuthProviderStatus.A)
                .orElseThrow(NotExistEmailAuthTokenException::new);

        emailAuth.useToken();
        return true;
    }

    @Override
    public boolean checkDuplicateNickname(String nickname) {
        isCorrectNickname(nickname);
        return emailAuthQueryRepository.isNotExistNickname(nickname);
    }

    @Override
    public boolean checkDuplicateAccountId(String accountId) {
        isCorrectAccountId(accountId);
        return emailAuthQueryRepository.isNotExistAccountId(accountId);
    }


    @Transactional
    @Override
    public void checkEmailLinkage(EmailLinkageCheckRequestDto requestDto) {
        EmailLinkage emailLinkage = emailLinkageQueryRepository.findValidLinkageByEmail(
                        requestDto.getAccountId(), requestDto.getEmail(),
                        requestDto.getAuthToken(), EMAIL_AUTH_MINUTE.getDateTime())
                .orElseThrow(NotExistEmailAuthTokenException::new);

        Member member =
                memberRepository
                        .findOneByAccountId(requestDto.getAccountId()).orElseThrow(NotExistMemberException::new);

        String provider = getProviderByEmail(requestDto.getEmail());

        emailLinkage.useToken();

        if (GOOGLE.getValue().equals(provider)) {
            member.updateGoogleEmail(requestDto.getEmail());
        } else if (NAVER.getValue().equals(provider)) {
            member.updateNaverEmail(requestDto.getEmail());
        } else {
            member.updateKakaoEmail(requestDto.getEmail());
        }
    }

    @Transactional
    @Override
    public void updateEmailLinkage(String email, String accountId) {
        validateDuplicateAtLinkage(email);

        EmailLinkage emailAuth = emailLinkageRepository.save(EmailLinkage.builder()
                .email(email)
                .accountId(accountId)
                .authToken(UUID.randomUUID().toString())
                .expired(false)
                .build());

        emailService.sendForLinkageEmail(emailAuth.getEmail(), emailAuth.getAuthToken(), emailAuth.getAccountId());
    }

    @Transactional
    @Override
    public MemberLoginResponseDto loginMember(MemberLoginRequestDto requestDto) {
        isCorrectLoginRequest(requestDto);
        Member member =
                memberRepository.findOneByAccountId(requestDto.getAccountId()).orElseThrow(LoginFailureException::new);

        if (!passwordEncoder.matches(requestDto.getAccountPw(), member.getAccountPw())) {
            throw new LoginFailureException();
        } else {
            member.updateRefreshToken(jwtTokenProvider.createRefreshToken());
            return new MemberLoginResponseDto(
                    member.getId(), jwtTokenProvider.createToken(requestDto.getAccountId()), member.getRefreshToken());
        }
    }

    @Transactional
    @Override
    public TokenResponseDto reIssue(TokenRequestDto requestDto) {
        if (!jwtTokenProvider.validateTokenExpiration(requestDto.getRefreshToken())) {
            throw new InvalidRefreshTokenException();
        }
        Member member = findMemberByToken(requestDto);

        if (!member.getRefreshToken().equals(requestDto.getRefreshToken())) {
            throw new InvalidRefreshTokenException();
        }
        String accessToken = jwtTokenProvider.createToken(member.getAccountId());
        String refreshToken = jwtTokenProvider.createRefreshToken();
        member.updateRefreshToken(refreshToken);

        return new TokenResponseDto(accessToken, refreshToken);
    }

    @Transactional
    @Override
    public Long insertFollowedMemberByMemberId(Long memberId, Long followedMemberId)
            throws NotExistMemberException, AlreadyFollowedMemberException {
        isExistMember(followedMemberId);

        if (memberQueryRepository.isExistFollowedMember(memberId, followedMemberId)) {
            throw new AlreadyFollowedMemberException();
        }

        return followedMemberRepository
                .save(
                        FollowedMember.createFollowedMember(
                                Member.createMember(memberId),
                                Member.createMember(followedMemberId)))
                .getId();
    }

    @Transactional
    @Override
    public Long insertBlockedMemberByMemberId(Long memberId, Long blockedMemberId,
                                              BlockedMemberStatus blockedMemberStatus)
            throws NotExistMemberException, AlreadyBlockedMemberException {

        isExistMember(blockedMemberId);
        isExistBlock(memberId, blockedMemberId);

        followedMemberRepository.findByFollowingMemberIdAndMemberId(memberId, blockedMemberId)
                .ifPresent(followedMemberRepository::delete);
        followedMemberRepository.findByFollowingMemberIdAndMemberId(blockedMemberId, memberId)
                .ifPresent(followedMemberRepository::delete);

        return blockedMemberRepository
                .save(
                        BlockedMember.createBlockedMember(
                                Member.createMember(memberId),
                                Member.createMember(blockedMemberId),
                                blockedMemberStatus))
                .getId();
    }

    @Transactional
    @Override
    public Long insertSearch(Long memberId, String content, SearchedByMemberStatus searchedByMemberStatus) throws NotExistMemberException {

        Member member = getMemberWithSearch(memberId);

        return member.addSearch(content, searchedByMemberStatus);
    }

    private void isExistBlock(Long memberId, Long blockedMemberId) {
        if (memberQueryRepository.isExistBlockedMember(memberId, blockedMemberId)) {
            throw new AlreadyBlockedMemberException();
        }
    }

    private void isExistMember(Long memberId) {
        if (!memberQueryRepository.isExist(memberId)) {
            throw new NotExistMemberException();
        }
    }

    private void validateDuplicateAtRegister(String accountId, String nickname, String email) {

        List<Member> memberList = memberRepository
                .findAllByAccountIdOrNicknameOrEmail(accountId, nickname, email);
        if (memberList.isEmpty()) {

        } else if (memberList.stream().anyMatch(member -> member.getAccountId().equals(accountId))) {
            throw new AlreadyAccountIdException();
        } else if (memberList.stream().anyMatch(member -> member.getNickname().equals(nickname))) {
            throw new AlreadyNicknameException();
        } else {
            throw new AlreadyEmailException();
        }
    }

    private void validateDuplicateAtLinkage(String email) {

        String provider = getProviderByEmail(email);
        Optional<Member> optionalMember;

        if (NAVER.getValue().equals(provider)) {
            optionalMember = memberRepository.findOneByNaverEmail(email);
        } else if (KAKAO.getValue().equals(provider)) {
            optionalMember = memberRepository.findOneByKakaoEmail(email);
        } else {
            optionalMember = memberRepository.findOneByGoogleEmail(email);
        }

        if (optionalMember.isPresent()) {
            throw new AlreadyLinkageEmailException();
        }
    }

    private String getProviderByEmail(String email) {
        return email.substring(email.indexOf("@") + 1, email.indexOf("."));
    }

    public Member findMemberByToken(TokenRequestDto requestDto) {
        Authentication auth = jwtTokenProvider.getAuthentication(requestDto.getAccessToken());
        UserDetails userDetails = (UserDetails) auth.getPrincipal();
        String accountId = userDetails.getUsername();
        return memberRepository.findOneByAccountId(accountId).orElseThrow(NotExistMemberException::new);
    }

    private Member getMemberWithSearch(Long memberId) throws NotExistMemberException {
        Optional<Member> optionalMember = memberRepository.findOneByIdWithSearch(memberId);

        return optionalMember.orElseThrow(NotExistMemberException::new);
    }

    private void isExistAuthEmail(String email, String authToken) {
        if (!emailAuthQueryRepository.isExistAuthEmail(email, authToken,
                REGISTER_EMAIL_AUTH_MINUTE.getDateTime(), EmailAuthProviderStatus.A)) {
            throw new EmailNotAuthenticatedException();
        }
    }

    private void isCorrectRegisterRequest(MemberRegisterRequestDto requestDto) {
        isCorrectAccountId(requestDto.getAccountId());
        isCorrectAccountPw(requestDto.getAccountPw());
        isCorrectNickname(requestDto.getNickname());
        isCorrectEmail(requestDto.getEmail());
    }

    private void isCorrectLoginRequest(MemberLoginRequestDto requestDto) {
        isCorrectAccountId(requestDto.getAccountId());
        isCorrectAccountPw(requestDto.getAccountPw());
    }

    private void isCorrectEmail(String email) {
        if (MemberCondition.EMAIL.isNotCorrect(email)) {
            throw new InCorrectEmailException();
        }
    }

    private void isCorrectAccountId(String accountId) {
        if (MemberCondition.ACCOUNT_ID.isNotCorrect(accountId)) {
            throw new InCorrectAccountIdException();
        }
    }

    private void isCorrectAccountPw(String accountPw) {
        isAccountPwCombination(accountPw);
        if (MemberCondition.ACCOUNT_PW.isNotCorrect(accountPw)) {
            throw new InCorrectAccountPwException();
        }
    }

    private void isCorrectNickname(String nickname) {
        if (MemberCondition.NICKNAME.isNotCorrect(nickname)) {
            throw new InCorrectNicknameException();
        }
    }

    public void checkDuplicateEmail(String email) {
        if (emailAuthQueryRepository.isExistEmail(email)) {
            throw new AlreadyEmailException();
        }
    }
    private void isAccountPwCombination(String accountPw) {
        if (isIncludeAllEnglish(accountPw)) {
            throw new InCorrectAccountPwException();
        } else if (isIncludeAllSign(accountPw)) {
            throw new InCorrectAccountPwException();
        } else if (isIncludeAllNumber(accountPw)) {
            throw new InCorrectAccountPwException();
        }
    }

    private boolean isIncludeAllNumber(String accountPw) {
        return !MemberCondition.NUMBER.isNotCorrect(accountPw);
    }

    private boolean isIncludeAllSign(String accountPw) {
        return !MemberCondition.SIGN.isNotCorrect(accountPw);
    }

    private boolean isIncludeAllEnglish(String accountPw) {
        return !MemberCondition.ENGLISH.isNotCorrect(accountPw);
    }
}
