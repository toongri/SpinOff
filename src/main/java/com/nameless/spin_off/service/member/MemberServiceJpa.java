package com.nameless.spin_off.service.member;

import com.nameless.spin_off.config.auth.ProviderService;
import com.nameless.spin_off.config.auth.dto.AccessToken;
import com.nameless.spin_off.config.auth.dto.profile.ProfileDto;
import com.nameless.spin_off.config.jwt.JwtTokenProvider;
import com.nameless.spin_off.dto.MemberDto.*;
import com.nameless.spin_off.entity.collection.Collection;
import com.nameless.spin_off.entity.collection.FollowedCollection;
import com.nameless.spin_off.entity.enums.ErrorEnum;
import com.nameless.spin_off.entity.enums.member.*;
import com.nameless.spin_off.entity.member.*;
import com.nameless.spin_off.exception.member.AlreadyAccountIdException;
import com.nameless.spin_off.exception.member.AlreadyBlockedMemberException;
import com.nameless.spin_off.exception.member.AlreadyFollowedMemberException;
import com.nameless.spin_off.exception.member.NotExistMemberException;
import com.nameless.spin_off.exception.security.DontHaveAuthorityException;
import com.nameless.spin_off.exception.security.InvalidRefreshTokenException;
import com.nameless.spin_off.exception.sign.*;
import com.nameless.spin_off.repository.collection.CollectionRepository;
import com.nameless.spin_off.repository.collection.FollowedCollectionRepository;
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
import java.util.stream.Collectors;

import static com.nameless.spin_off.entity.enums.ContentsLengthEnum.*;
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
    private final FollowedCollectionRepository followedCollectionRepository;
    private final ProviderService providerService;

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
    public boolean sendEmailForAuth(String email)
            throws AlreadyAccountIdException, AlreadyNicknameException {

        isCorrectEmail(email);
        checkDuplicateEmail(email);

        EmailAuth emailAuth = emailAuthRepository.save(EmailAuth.builder()
                .email(email)
                .authToken(RandomStringUtils.randomAlphabetic(EMAIL_TOKEN.getLength()))
                .expired(false)
                .provider(EmailAuthProviderStatus.A)
                .build());

        emailService.sendForRegister(emailAuth.getEmail(), emailAuth.getAuthToken());
        return true;
    }

    @Override
    public boolean sendEmailForAccountId(String email) {
        isCorrectEmail(email);
        String accountId = memberQueryRepository.findAccountIdByEmail(email);
        if (accountId == null) {
            throw new NotExistMemberException(ErrorEnum.NOT_EXIST_MEMBER);
        } else {
            emailService.sendForAccountId(email, accountId);
        }
        return true;
    }

    @Transactional
    @Override
    public boolean sendEmailForAccountPw(String accountId) {
        isCorrectAccountId(accountId);
        Optional<Member> optionalMember = memberRepository.findOneByAccountId(accountId);

        Member member = optionalMember
                .orElseThrow(() -> new NotExistMemberException(ErrorEnum.NOT_EXIST_MEMBER));

        String randomPassword = getRandomPassword();
        member.updateAccountPw(passwordEncoder.encode(randomPassword));

        emailService.sendForAccountPw(member.getEmail(), randomPassword);
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
                .orElseThrow(() -> new NotExistEmailAuthTokenException(ErrorEnum.NOT_EXIST_EMAIL_AUTH_TOKEN));

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
                .orElseThrow(() -> new NotExistEmailAuthTokenException(ErrorEnum.NOT_EXIST_EMAIL_AUTH_TOKEN));

        Member member =
                memberRepository
                        .findOneByAccountId(requestDto.getAccountId())
                        .orElseThrow(() -> new NotExistMemberException(ErrorEnum.NOT_EXIST_MEMBER));

        String provider = getProviderByEmail(requestDto.getEmail());

        emailLinkage.useToken();

        if (google.getValue().equals(provider)) {
            member.updateGoogleEmail(requestDto.getEmail());
        } else if (naver.getValue().equals(provider)) {
            member.updateNaverEmail(requestDto.getEmail());
        } else {
            member.updateKakaoEmail(requestDto.getEmail());
        }
    }

    @Transactional
    @Override
    public void updateEmailLinkage(String email, String accountId, String provider) {
        validateDuplicateAtLinkage(email, provider);

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
                memberRepository.findOneByAccountId(requestDto.getAccountId())
                        .orElseThrow(() -> new NotExistAccountIdException(ErrorEnum.NOT_EXIST_ACCOUNT_ID));

        if (!passwordEncoder.matches(requestDto.getAccountPw(), member.getAccountPw())) {
            throw new NotMatchAccountPwException(ErrorEnum.NOT_MATCH_ACCOUNT_PW);
        } else {
            member.updateRefreshToken(jwtTokenProvider.createRefreshToken());
            return new MemberLoginResponseDto(
                    member.getId(), jwtTokenProvider.createToken(requestDto.getAccountId()), member.getRefreshToken());
        }
    }

    @Transactional
    @Override
    public MemberLoginResponseDto loginBySocial(String authCode, String provider) {
        AccessToken accessToken = providerService.getAccessToken(authCode, provider);
        ProfileDto profile = providerService.getProfile(accessToken.getAccess_token(), provider);
        Member member = getMemberByProvider(profile.getEmail(), provider)
                .orElseGet(() -> saveMember(profile, provider));

        member.updateRefreshToken(jwtTokenProvider.createRefreshToken());
        return new MemberLoginResponseDto(
                member.getId(),
                jwtTokenProvider.createToken(member.getAccountId()),
                member.getRefreshToken());
    }

    @Transactional
    @Override
    public TokenResponseDto reIssue(TokenRequestDto requestDto) {
        if (!jwtTokenProvider.validateTokenExpiration(requestDto.getRefreshToken())) {
            throw new InvalidRefreshTokenException(ErrorEnum.INVALID_REFRESH_TOKEN);
        }
        Member member = findMemberByToken(requestDto);

        if (!member.getRefreshToken().equals(requestDto.getRefreshToken())) {
            throw new InvalidRefreshTokenException(ErrorEnum.INVALID_REFRESH_TOKEN);
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
        isBlockedOrBlockingAboutAll(memberId, followedMemberId);

        isExistFollowedMember(memberId, followedMemberId);

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
        if (BlockedMemberStatus.B.equals(blockedMemberStatus)) {
            isExistBlock(memberId, blockedMemberId);
        } else if (BlockedMemberStatus.A.equals(blockedMemberStatus)) {
            isExistBlockAndDeletePastBlocked(memberId, blockedMemberId);

            followedMemberRepository
                    .deleteAll(getFollowedMembersByFollowingMemberIdAndMemberId(memberId, blockedMemberId));

            followedCollectionRepository
                    .deleteAll(getFollowedCollectionsByFollowingMemberIdAndMemberId(memberId, blockedMemberId));
        }

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
            throw new AlreadyBlockedMemberException(ErrorEnum.ALREADY_BLOCKED_MEMBER);
        }
    }

    private void isExistBlockAndDeletePastBlocked(Long memberId, Long blockedMemberId) {
        Optional<BlockedMember> optional = 
                memberQueryRepository.findOneByBlockingIdAndBlockedId(memberId, blockedMemberId);
        if (optional.isPresent()) {
            BlockedMember blockedMember = optional.get();
            if (BlockedMemberStatus.A.equals(blockedMember.getBlockedMemberStatus())) {
                throw new AlreadyBlockedMemberException(ErrorEnum.ALREADY_BLOCKED_MEMBER);
            } else {
                blockedMemberRepository.delete(blockedMember);
            }
        }
    }

    private void isExistMember(Long memberId) {
        if (!memberQueryRepository.isExist(memberId)) {
            throw new NotExistMemberException(ErrorEnum.NOT_EXIST_MEMBER);
        }
    }

    private void validateDuplicateAtRegister(String accountId, String nickname, String email) {

        List<Member> memberList = memberRepository
                .findAllByAccountIdOrNicknameOrEmail(accountId, nickname, email);
        if (memberList.isEmpty()) {

        } else if (memberList.stream().anyMatch(member -> member.getAccountId().equals(accountId))) {
            throw new AlreadyAccountIdException(ErrorEnum.ALREADY_ACCOUNT_ID);
        } else if (memberList.stream().anyMatch(member -> member.getNickname().equals(nickname))) {
            throw new AlreadyNicknameException(ErrorEnum.ALREADY_NICKNAME);
        } else {
            throw new AlreadyEmailException(ErrorEnum.ALREADY_EMAIL);
        }
    }

    private void validateDuplicateAtLinkage(String email, String provider) {
        Optional<Member> optionalMember = getMemberByProvider(email, provider);

        if (optionalMember.isPresent()) {
            throw new AlreadyLinkageEmailException(ErrorEnum.ALREADY_LINKAGE_EMAIL);
        }
    }

    private Optional<Member> getMemberByProvider(String email, String provider) {
        if (naver.getName().equals(provider)) {
            return memberRepository.findOneByNaverEmail(email);
        } else if (kakao.getName().equals(provider)) {
            return memberRepository.findOneByKakaoEmail(email);
        } else {
            return memberRepository.findOneByGoogleEmail(email);
        }
    }

    private Member saveMember(ProfileDto profile, String provider) {
        MemberBuilder memberBuilder = Member.buildMember()
                .setEmail(profile.getEmail())
                .setAccountId(getRandomAccountId())
                .setNickname(getRandomNickname())
                .setName(profile.getName())
                .setAccountPw(null);
        Member member;

        if (naver.getName().equals(provider)) {
            member = memberBuilder.setNaverEmail(profile.getEmail()).build();
        } else if (kakao.getName().equals(provider)) {
            member = memberBuilder.setKakaoEmail(profile.getEmail()).build();
        } else {
            member = memberBuilder.setGoogleEmail(profile.getEmail()).build();
        }
        memberRepository.save(member);
        return member;
    }

    private String getProviderByEmail(String email) {
        return email.substring(email.indexOf("@") + 1, email.indexOf("."));
    }

    public Member findMemberByToken(TokenRequestDto requestDto) {
        Authentication auth = jwtTokenProvider.getAuthentication(requestDto.getAccessToken());
        UserDetails userDetails = (UserDetails) auth.getPrincipal();
        String accountId = userDetails.getUsername();
        return memberRepository.findOneByAccountId(accountId)
                .orElseThrow(() -> new NotExistMemberException(ErrorEnum.NOT_EXIST_MEMBER));
    }

    private Member getMemberWithSearch(Long memberId) throws NotExistMemberException {
        Optional<Member> optionalMember = memberRepository.findOneByIdWithSearch(memberId);

        return optionalMember.orElseThrow(() -> new NotExistMemberException(ErrorEnum.NOT_EXIST_MEMBER));
    }

    private void isExistAuthEmail(String email, String authToken) {
        if (!emailAuthQueryRepository.isExistAuthEmail(email, authToken,
                REGISTER_EMAIL_AUTH_MINUTE.getDateTime(), EmailAuthProviderStatus.A)) {
            throw new EmailNotAuthenticatedException(ErrorEnum.EMAIL_NOT_AUTHENTICATED);
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
            throw new IncorrectEmailException(ErrorEnum.INCORRECT_EMAIL);
        }
    }

    private void isCorrectAccountId(String accountId) {
        if (MemberCondition.ACCOUNT_ID.isNotCorrect(accountId)) {
            throw new IncorrectAccountIdException(ErrorEnum.INCORRECT_ACCOUNT_ID);
        }
    }

    private void isCorrectAccountPw(String accountPw) {
        isAccountPwCombination(accountPw);
        if (MemberCondition.ACCOUNT_PW.isNotCorrect(accountPw)) {
            throw new IncorrectAccountPwException(ErrorEnum.INCORRECT_ACCOUNT_PW);
        }
    }

    private void isCorrectNickname(String nickname) {
        if (MemberCondition.NICKNAME.isNotCorrect(nickname)) {
            throw new IncorrectNicknameException(ErrorEnum.INCORRECT_NICKNAME);
        }
    }

    public void checkDuplicateEmail(String email) {
        if (emailAuthQueryRepository.isExistEmail(email)) {
            throw new AlreadyEmailException(ErrorEnum.ALREADY_EMAIL);
        }
    }
    private void isAccountPwCombination(String accountPw) {
        if (isIncludeAllEnglish(accountPw)) {
            throw new IncorrectAccountPwException(ErrorEnum.INCORRECT_ACCOUNT_PW);
        } else if (isIncludeAllSign(accountPw)) {
            throw new IncorrectAccountPwException(ErrorEnum.INCORRECT_ACCOUNT_PW);
        } else if (isIncludeAllNumber(accountPw)) {
            throw new IncorrectAccountPwException(ErrorEnum.INCORRECT_ACCOUNT_PW);
        }
    }

    private String getRandomPassword() {
        String randomAccountId = RandomStringUtils.randomAlphabetic(ACCOUNT_PW_MIN.getLength());

        randomAccountId += RandomStringUtils.randomNumeric(ACCOUNT_PW_MIN.getLength());

        return randomAccountId;
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

    private void isExistFollowedMember(Long memberId, Long followedMemberId) {
        if (memberQueryRepository.isExistFollowedMember(memberId, followedMemberId)) {
            throw new AlreadyFollowedMemberException(ErrorEnum.ALREADY_FOLLOWED_MEMBER);
        }
    }

    private void isBlockedOrBlockingAboutAll(Long memberId, Long targetMemberId) {
        if (memberQueryRepository.isBlockedOrBlockingAboutAll(memberId, targetMemberId)) {
            throw new DontHaveAuthorityException(ErrorEnum.DONT_HAVE_AUTHORITY);
        }
    }

    private List<FollowedCollection> getFollowedCollectionsByFollowingMemberIdAndMemberId(
            Long memberId, Long followedMemberId) {
        return followedCollectionRepository
                .findAllIdByFollowingMemberIdAndMemberId(memberId, followedMemberId)
                .stream()
                .map(FollowedCollection::createFollowedCollection)
                .collect(Collectors.toList());
    }

    private List<FollowedMember> getFollowedMembersByFollowingMemberIdAndMemberId(Long memberId, Long followedMemberId) {
        return followedMemberRepository
                .findAllIdByFollowingMemberIdAndMemberId(memberId, followedMemberId)
                .stream()
                .map(FollowedMember::createFollowedMember)
                .collect(Collectors.toList());
    }

    private String getRandomNickname() {
        String randomNickname = RandomStringUtils.randomAlphabetic(NICKNAME_MAX.getLength());

        Optional<Member> member = memberRepository.findByNickname(randomNickname);

        while (member.isPresent()) {
            randomNickname = RandomStringUtils.randomAlphabetic(NICKNAME_MAX.getLength());
            member = memberRepository.findByNickname(randomNickname);
        }
        log.info("randomNickname : {}", randomNickname);
        return randomNickname;
    }

    private String getRandomAccountId() {
        String randomAccountId = RandomStringUtils.randomAlphabetic(ACCOUNT_ID_MAX.getLength());

        Optional<Member> member = memberRepository.findOneByAccountId(randomAccountId);

        while (member.isPresent()) {
            randomAccountId = RandomStringUtils.randomAlphabetic(ACCOUNT_ID_MAX.getLength());
            member = memberRepository.findByNickname(randomAccountId);
        }
        log.info("randomNickname : {}", randomAccountId);
        return randomAccountId;
    }
}
