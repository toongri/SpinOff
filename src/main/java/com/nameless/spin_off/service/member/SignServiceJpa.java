package com.nameless.spin_off.service.member;

import com.nameless.spin_off.config.auth.ProviderService;
import com.nameless.spin_off.config.auth.dto.AccessToken;
import com.nameless.spin_off.config.auth.dto.profile.ProfileDto;
import com.nameless.spin_off.config.jwt.JwtTokenProvider;
import com.nameless.spin_off.dto.MemberDto;
import com.nameless.spin_off.dto.MemberDto.*;
import com.nameless.spin_off.entity.collection.Collection;
import com.nameless.spin_off.entity.member.EmailAuth;
import com.nameless.spin_off.entity.member.EmailLinkage;
import com.nameless.spin_off.entity.member.Member;
import com.nameless.spin_off.enums.ErrorEnum;
import com.nameless.spin_off.enums.member.EmailAuthProviderStatus;
import com.nameless.spin_off.enums.member.MemberCondition;
import com.nameless.spin_off.exception.member.AlreadyAccountIdException;
import com.nameless.spin_off.exception.member.NotExistMemberException;
import com.nameless.spin_off.exception.security.InvalidRefreshTokenException;
import com.nameless.spin_off.exception.sign.*;
import com.nameless.spin_off.repository.collection.CollectionRepository;
import com.nameless.spin_off.repository.member.EmailAuthRepository;
import com.nameless.spin_off.repository.member.EmailLinkageRepository;
import com.nameless.spin_off.repository.member.MemberRepository;
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

import java.util.Optional;
import java.util.UUID;

import static com.nameless.spin_off.enums.ContentsLengthEnum.*;
import static com.nameless.spin_off.enums.ContentsTimeEnum.EMAIL_AUTH_MINUTE;
import static com.nameless.spin_off.enums.ContentsTimeEnum.REGISTER_EMAIL_AUTH_MINUTE;
import static com.nameless.spin_off.enums.member.EmailLinkageServiceEnum.*;

@Slf4j
@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class SignServiceJpa implements SignService{

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
    private final ProviderService providerService;
    private boolean isRegister;

    @Transactional
    @Override
    public MemberLoginResponseDto registerMember(MemberRegisterRequestDto requestDto)
            throws AlreadyAccountIdException, AlreadyNicknameException {

        isCorrectAccountPw(requestDto.getAccountPw());
        isExistAuthEmail(requestDto.getEmail(), requestDto.getAuthToken());

        Member member = Member.buildMember()
                .setNickname(requestDto.getNickname())
                .setEmail(requestDto.getEmail())
                .setAccountId(requestDto.getAccountId())
                .setAccountPw(passwordEncoder.encode(requestDto.getAccountPw()))
                .setBirth(requestDto.getBirth())
                .setName(requestDto.getName())
                .setPhoneNumber(requestDto.getPhoneNumber())
                .build();

        member.updateRefreshToken(jwtTokenProvider.createRefreshToken());

        memberRepository.save(member);
        collectionRepository.save(Collection.createDefaultCollection(member));

        return new MemberLoginResponseDto(
                member.getId(), jwtTokenProvider.createToken(requestDto.getAccountId()), member.getRefreshToken());
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
    public SocialLoginResponseDto loginBySocial(String authCode, String provider) {
        AccessToken accessToken = providerService.getAccessToken(authCode, provider);
        ProfileDto profile = providerService.getProfile(accessToken.getAccess_token(), provider);

        isRegister = false;
        Member member = getMemberByProvider(profile.getEmail(), provider)
                .orElseGet(() -> saveMember(profile, provider));

        member.updateRefreshToken(jwtTokenProvider.createRefreshToken());
        return new SocialLoginResponseDto(
                isRegister,
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
    public boolean confirmEmail(EmailAuthRequestDto requestDto) {

        EmailAuth emailAuth = emailAuthQueryRepository.findValidAuthByEmail(
                        requestDto.getEmail(),
                        requestDto.getAuthToken(),
                        EMAIL_AUTH_MINUTE.getDateTimeMinusMinutes(),
                        EmailAuthProviderStatus.A)
                .orElseThrow(() -> new NotExistEmailAuthTokenException(ErrorEnum.NOT_EXIST_EMAIL_AUTH_TOKEN));

        emailAuth.useToken();
        return true;
    }

    @Transactional
    @Override
    public void checkEmailLinkage(EmailLinkageCheckRequestDto requestDto) {
        EmailLinkage emailLinkage = emailLinkageQueryRepository.findValidLinkageByEmail(
                        requestDto.getAccountId(), requestDto.getEmail(),
                        requestDto.getAuthToken(), EMAIL_AUTH_MINUTE.getDateTimeMinusMinutes())
                .orElseThrow(() -> new NotExistEmailAuthTokenException(ErrorEnum.NOT_EXIST_EMAIL_AUTH_TOKEN));

        Member member =
                getMemberByAccountId(requestDto.getAccountId());

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
    public boolean sendEmailForAuth(String email) throws AlreadyAccountIdException, AlreadyNicknameException {

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
        emailService.sendForForgetAccountId(email, memberQueryRepository.findAccountIdByEmail(email)
                .orElseThrow(() -> new NotExistMemberException(ErrorEnum.NOT_EXIST_MEMBER)));
        return true;
    }

    @Transactional
    @Override
    public boolean sendEmailForAccountPw(String accountId) {
        isCorrectAccountId(accountId);
        Member member = getMemberByAccountId(accountId);

        String randomPassword = getRandomPassword();
        member.updateAccountPw(passwordEncoder.encode(randomPassword));

        emailService.sendForForgetAccountPw(member.getEmail(), randomPassword);

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

    private Member getMemberByAccountId(String accountId) {
        return memberRepository.findOneByAccountId(accountId).orElseThrow(() -> new NotExistMemberException(ErrorEnum.NOT_EXIST_MEMBER));
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

        Member member = Member.createMember(getRandomAccountId(), getRandomNickname(), profile.getName(), profile.getEmail());

        if (naver.getName().equals(provider)) {
            member.updateNaverEmail(profile.getEmail());
        } else if (kakao.getName().equals(provider)) {
            member.updateKakaoEmail(profile.getEmail());
        } else {
            member.updateGoogleEmail(profile.getEmail());
        }
        memberRepository.save(member);
        collectionRepository.save(Collection.createDefaultCollection(member));
        isRegister = true;
        return member;
    }

    private String getProviderByEmail(String email) {
        return email.substring(email.indexOf("@") + 1, email.indexOf("."));
    }

    public Member findMemberByToken(TokenRequestDto requestDto) {
        Authentication auth = jwtTokenProvider.getAuthentication(requestDto.getAccessToken());
        UserDetails userDetails = (UserDetails) auth.getPrincipal();
        return getMemberByAccountId(userDetails.getUsername());
    }

    private void isExistAuthEmail(String email, String authToken) {
        if (!emailAuthQueryRepository.isExistAuthEmail(email, authToken,
                REGISTER_EMAIL_AUTH_MINUTE.getDateTimeMinusMinutes(), EmailAuthProviderStatus.A)) {
            throw new EmailNotAuthenticatedException(ErrorEnum.EMAIL_NOT_AUTHENTICATED);
        }
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

    private String getRandomPassword() {
        String randomAccountId = RandomStringUtils.randomAlphabetic(ACCOUNT_PW_MIN.getLength());

        randomAccountId += RandomStringUtils.randomNumeric(ACCOUNT_PW_MIN.getLength());

        return randomAccountId;
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
