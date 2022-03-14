package com.nameless.spin_off.service.member;

import com.nameless.spin_off.config.jwt.JwtTokenProvider;
import com.nameless.spin_off.dto.MemberDto.*;
import com.nameless.spin_off.entity.collection.Collection;
import com.nameless.spin_off.entity.enums.member.BlockedMemberStatus;
import com.nameless.spin_off.entity.enums.member.EmailAuthProviderStatus;
import com.nameless.spin_off.entity.enums.member.SearchedByMemberStatus;
import com.nameless.spin_off.entity.member.EmailAuth;
import com.nameless.spin_off.entity.member.EmailLinkage;
import com.nameless.spin_off.entity.member.Member;
import com.nameless.spin_off.exception.member.*;
import com.nameless.spin_off.exception.security.InvalidRefreshTokenException;
import com.nameless.spin_off.repository.collection.CollectionRepository;
import com.nameless.spin_off.repository.member.EmailAuthRepository;
import com.nameless.spin_off.repository.member.EmailLinkageRepository;
import com.nameless.spin_off.repository.member.MemberRepository;
import com.nameless.spin_off.repository.query.EmailAuthQueryRepository;
import com.nameless.spin_off.repository.query.EmailLinkageQueryRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

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

    @Transactional()
    @Override
    public Long insertMemberByMemberVO(MemberRegisterRequestDto memberVO)
            throws AlreadyAccountIdException, AlreadyNicknameException {

        validateDuplicateAtRegister(memberVO.getAccountId(), memberVO.getNickname(), memberVO.getEmail());

        Member member = memberRepository.save(Member.createMemberByCreateVO(memberVO));
        collectionRepository.save(Collection.createDefaultCollection(member));
        return member.getId();
    }

    @Transactional()
    @Override
    public MemberRegisterResponseDto registerMember(MemberRegisterRequestDto requestDto)
            throws AlreadyAccountIdException, AlreadyNicknameException {
        validateDuplicateAtRegister(requestDto.getAccountId(), requestDto.getNickname(), requestDto.getEmail());

        EmailAuth emailAuth = emailAuthRepository.save(EmailAuth.builder()
                .email(requestDto.getEmail())
                .authToken(UUID.randomUUID().toString())
                .expired(false)
                .provider(EmailAuthProviderStatus.A)
                .build());

        Member member = memberRepository.save(Member.buildMember()
                .setNickname(requestDto.getNickname())
                .setEmail(requestDto.getEmail())
                .setAccountId(requestDto.getAccountId())
                .setAccountPw(passwordEncoder.encode(requestDto.getAccountPw()))
                .setBirth(requestDto.getBirth())
                .setName(requestDto.getName())
                .build());

        emailService.sendForRegister(emailAuth.getEmail(), emailAuth.getAuthToken());
        return MemberRegisterResponseDto.builder()
                .id(member.getId())
                .accountId(member.getAccountId())
                .build();
    }

    @Transactional
    @Override
    public void confirmEmail(EmailAuthRequestDto requestDto) {
        EmailAuth emailAuth = emailAuthQueryRepository.findValidAuthByEmail(
                requestDto.getEmail(), requestDto.getAuthToken(), LocalDateTime.now(), EmailAuthProviderStatus.A)
                .orElseThrow(NotExistEmailAuthTokenException::new);
        Member member =
                memberRepository.findOneByEmail(requestDto.getEmail()).orElseThrow(NotExistMemberException::new);
        emailAuth.useToken();
        member.updateEmailAuth(true);
    }

    @Transactional
    @Override
    public void emailLinkageCheck(EmailLinkageCheckRequestDto requestDto) {
        EmailLinkage emailLinkage = emailLinkageQueryRepository.findValidLinkageByEmail(
                        requestDto.getAccountId(), requestDto.getEmail(),
                        requestDto.getAuthToken(), LocalDateTime.now())
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
    public void emailLinkageUpdate(String email, String accountId) {
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
        Member member =
                memberRepository.findOneByAccountId(requestDto.getAccountId()).orElseThrow(LoginFailureException::new);

        if (!passwordEncoder.matches(requestDto.getAccountPw(), member.getAccountPw())) {
            throw new LoginFailureException();

        } else if (!member.getEmailAuth()) {
            throw new EmailNotAuthenticatedException();
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

    @Transactional()
    @Override
    public Long insertFollowedMemberByMemberId(Long memberId, Long followedMemberId)
            throws NotExistMemberException, AlreadyFollowedMemberException {

        Member member = getMemberByIdWithFollowedMember(memberId);
        Member followedMember = getMemberByIdWithFollowingMember(followedMemberId);

        return member.addFollowedMember(followedMember);
    }

    @Transactional()
    @Override
    public Long insertBlockedMemberByMemberId(Long memberId, Long blockedMemberId, BlockedMemberStatus blockedMemberStatus) throws NotExistMemberException, AlreadyBlockedMemberException {
        Member member = getMemberByIdWithBlockedMember(memberId);
        Member blockedMember = getMemberByIdWithBlockingMember(blockedMemberId);

        return member.addBlockedMember(blockedMember, blockedMemberStatus);
    }

    @Transactional()
    @Override
    public Long insertSearch(Long memberId, String content, SearchedByMemberStatus searchedByMemberStatus) throws NotExistMemberException {

        Member member = getMemberWithSearch(memberId);

        return member.addSearch(content, searchedByMemberStatus);
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

    private Member getMemberByIdWithFollowingMember(Long followedMemberId) throws NotExistMemberException {
        Optional<Member> optionalMember = memberRepository.findOneByIdWithFollowingMember(followedMemberId);

        return optionalMember.orElseThrow(NotExistMemberException::new);
    }

    private Member getMemberByIdWithBlockingMember(Long blockedMemberId) throws NotExistMemberException {
        Optional<Member> optionalMember = memberRepository.findOneByIdWithBlockingMember(blockedMemberId);

        return optionalMember.orElseThrow(NotExistMemberException::new);
    }

    private Member getMemberByIdWithFollowedMember(Long memberId) throws NotExistMemberException {
        Optional<Member> optionalMember = memberRepository.findOneByIdWithFollowedMember(memberId);

        return optionalMember.orElseThrow(NotExistMemberException::new);
    }

    private Member getMemberByIdWithBlockedMember(Long memberId) throws NotExistMemberException {
        Optional<Member> optionalMember = memberRepository.findOneByIdWithBlockedMember(memberId);

        return optionalMember.orElseThrow(NotExistMemberException::new);
    }

    private Member getMemberWithSearch(Long memberId) throws NotExistMemberException {
        Optional<Member> optionalMember = memberRepository.findOneByIdWithSearch(memberId);

        return optionalMember.orElseThrow(NotExistMemberException::new);
    }
}
