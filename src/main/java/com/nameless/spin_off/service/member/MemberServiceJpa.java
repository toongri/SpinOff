package com.nameless.spin_off.service.member;

import com.nameless.spin_off.config.member.MemberDetails;
import com.nameless.spin_off.dto.MemberDto.EmailAuthRequestDto;
import com.nameless.spin_off.dto.MemberDto.MemberInfoRequestDto;
import com.nameless.spin_off.dto.MemberDto.MemberProfileRequestDto;
import com.nameless.spin_off.entity.collection.FollowedCollection;
import com.nameless.spin_off.entity.member.BlockedMember;
import com.nameless.spin_off.entity.member.EmailAuth;
import com.nameless.spin_off.entity.member.FollowedMember;
import com.nameless.spin_off.entity.member.Member;
import com.nameless.spin_off.enums.ErrorEnum;
import com.nameless.spin_off.enums.member.*;
import com.nameless.spin_off.exception.member.*;
import com.nameless.spin_off.exception.security.DontHaveAuthorityException;
import com.nameless.spin_off.exception.sign.EmailNotAuthenticatedException;
import com.nameless.spin_off.exception.sign.IncorrectAccountPwException;
import com.nameless.spin_off.exception.sign.IncorrectEmailException;
import com.nameless.spin_off.exception.sign.NotExistEmailAuthTokenException;
import com.nameless.spin_off.repository.collection.FollowedCollectionRepository;
import com.nameless.spin_off.repository.member.BlockedMemberRepository;
import com.nameless.spin_off.repository.member.EmailAuthRepository;
import com.nameless.spin_off.repository.member.FollowedMemberRepository;
import com.nameless.spin_off.repository.member.MemberRepository;
import com.nameless.spin_off.repository.query.EmailAuthQueryRepository;
import com.nameless.spin_off.repository.query.MemberQueryRepository;
import com.nameless.spin_off.service.support.AwsS3Service;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.RandomStringUtils;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.time.LocalDate;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import static com.nameless.spin_off.enums.ContentsLengthEnum.EMAIL_TOKEN;
import static com.nameless.spin_off.enums.ContentsTimeEnum.EMAIL_AUTH_MINUTE;
import static com.nameless.spin_off.enums.ContentsTimeEnum.REGISTER_EMAIL_AUTH_MINUTE;
import static com.nameless.spin_off.enums.ErrorEnum.*;

@Slf4j
@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class MemberServiceJpa implements MemberService {

    private final MemberRepository memberRepository;
    private final MemberQueryRepository memberQueryRepository;
    private final FollowedMemberRepository followedMemberRepository;
    private final BlockedMemberRepository blockedMemberRepository;
    private final FollowedCollectionRepository followedCollectionRepository;
    private final PasswordEncoder passwordEncoder;
    private final AwsS3Service awsS3Service;
    private final EmailAuthRepository emailAuthRepository;
    private final EmailAuthQueryRepository emailAuthQueryRepository;
    private final EmailService emailService;

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

    @Transactional
    @Override
    public Long updateMemberProfile(Long memberId, MemberProfileRequestDto memberProfileRequestDto, MultipartFile multipartFile) throws IOException {
        Member member = getMember(memberId);
        Long cnt = 0L;
        if (!memberProfileRequestDto.getAccountId().equals(member.getAccountId())) {
            cnt++;
            member.updateAccountId(memberProfileRequestDto.getAccountId());
        }
        if (!memberProfileRequestDto.getNickname().equals(member.getNickname())) {
            cnt++;
            member.updateNickname(memberProfileRequestDto.getNickname());
        }
        if (!memberProfileRequestDto.getBio().equals(member.getBio())) {
            cnt++;
            member.updateBio(memberProfileRequestDto.getBio());
        }
        if (!memberProfileRequestDto.getWebsite().equals(member.getWebsite())) {
            cnt++;
            member.updateWebsite(memberProfileRequestDto.getWebsite());
        }
        if (member.getProfileImg() != null && multipartFile == null) {
            cnt++;
            awsS3Service.deleteFile(member.getProfileImg());
            member.updateProfileImg(null);
        } else if (multipartFile != null) {
            cnt++;
            if (member.getProfileImg() != null) {
                awsS3Service.deleteFile(member.getProfileImg());
            }
            member.updateProfileImg(getUrlByMultipartFile(multipartFile));
        }
        if (cnt > 0) {
            member.updateLastModifiedDate();
        }
        return cnt;
    }

    @Transactional
    @Override
    public Long updateMemberInfo(Long memberId, MemberInfoRequestDto memberInfoRequestDto) {

        Member member = getMember(memberId);
        Long cnt = 0L;
        if (!memberInfoRequestDto.getEmail().equals(member.getEmail())) {
            isExistAuthEmail(memberInfoRequestDto.getEmail(), memberInfoRequestDto.getAuthToken());
            cnt++;
            member.updateEmail(memberInfoRequestDto.getEmail());
        }
        if (!memberInfoRequestDto.getPhoneNumber().equals(member.getPhoneNumber())) {
            cnt++;
            member.updatePhoneNumber(memberInfoRequestDto.getPhoneNumber());
        }
        if (!memberInfoRequestDto.getBirth().equals(member.getBirth())) {
            cnt++;
            member.updateBirth(memberInfoRequestDto.getBirth());
        }
        member.updateLastModifiedDate();

        return cnt;
    }

    @Override
    public Boolean isMatchedPassword(MemberDetails currentMember, String password) {
        return passwordEncoder.matches(password, currentMember.getPassword());
    }

    @Transactional
    @Override
    public Boolean updateMemberPassword(Long memberId, String password) {
        isCorrectAccountPw(password);
        Member member = getMember(memberId);
        member.updateAccountPw(passwordEncoder.encode(password));
        member.updateLastModifiedDate();
        return true;
    }

    @Transactional
    @Override
    public boolean sendEmailForAuth(Long memberId, String email) {

        isCorrectEmail(email);
        Member member = getMember(memberId);

        if (!member.getEmail().equals(email)) {
            throw new NotMatchEmailException(NOT_MATCH_EMAIL);
        }

        EmailAuth emailAuth = emailAuthRepository.save(EmailAuth.builder()
                .email(member.getEmail())
                .authToken(RandomStringUtils.randomAlphabetic(EMAIL_TOKEN.getLength()))
                .expired(false)
                .provider(EmailAuthProviderStatus.B)
                .build());

        emailService.sendForAuthEmail(emailAuth.getEmail(), emailAuth.getAuthToken());
        return true;
    }

    @Transactional
    @Override
    public boolean confirmEmailForAuth(EmailAuthRequestDto requestDto) {

        EmailAuth emailAuth = emailAuthQueryRepository.findValidAuthByEmail(
                        requestDto.getEmail(),
                        requestDto.getAuthToken(),
                        EMAIL_AUTH_MINUTE.getDateTimeMinusMinutes(),
                        EmailAuthProviderStatus.B)
                .orElseThrow(() -> new NotExistEmailAuthTokenException(ErrorEnum.NOT_EXIST_EMAIL_AUTH_TOKEN));

        emailAuth.useToken();
        return true;
    }

    @Transactional
    @Override
    public boolean sendEmailForUpdateEmail(Long memberId, String email) {

        isCorrectEmail(email);
        Member member = getMember(memberId);

        if (member.getEmail().equals(email)) {
            throw new AlreadyRegisterEmailException(ALREADY_REGISTER_EMAIL);
        }

        EmailAuth emailAuth = emailAuthRepository.save(EmailAuth.builder()
                .email(email)
                .authToken(RandomStringUtils.randomAlphabetic(EMAIL_TOKEN.getLength()))
                .expired(false)
                .provider(EmailAuthProviderStatus.C)
                .build());

        emailService.sendForUpdateEmail(emailAuth.getEmail(), emailAuth.getAuthToken());

        return true;
    }

    @Transactional
    @Override
    public boolean confirmEmailForUpdateEMail(EmailAuthRequestDto requestDto) {
        EmailAuth emailAuth = emailAuthQueryRepository.findValidAuthByEmail(
                        requestDto.getEmail(),
                        requestDto.getAuthToken(),
                        EMAIL_AUTH_MINUTE.getDateTimeMinusMinutes(),
                        EmailAuthProviderStatus.C)
                .orElseThrow(() -> new NotExistEmailAuthTokenException(ErrorEnum.NOT_EXIST_EMAIL_AUTH_TOKEN));

        emailAuth.useToken();
        return true;
    }

    @Transactional
    @Override
    public boolean updateMemberDeleteDate(Long memberId, LocalDate localDate) {
        Member member = getMember(memberId);
        member.updateDeleteDate(localDate);
        return true;
    }

    @Transactional
    @Override
    public boolean updateMemberDeleteDateToNull(Long memberId) {
        Member member = getMemberByIdAndDeleteDateNotNull(memberId);
        member.updateDeleteDate(null);
        return true;
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

    private Member getMemberByIdAndDeleteDateNotNull(Long memberId) {
        return memberQueryRepository
                .findOneByIdAndDeleteDateNotNull(memberId).orElseThrow(() -> new DontHaveAuthorityException(DONT_HAVE_AUTHORITY));
    }

    private void isCorrectEmail(String email) {
        if (MemberCondition.EMAIL.isNotCorrect(email)) {
            throw new IncorrectEmailException(ErrorEnum.INCORRECT_EMAIL);
        }
    }

    private Member getMember(Long memberId) {
        return memberRepository.findById(memberId).orElseThrow(() -> new NotExistMemberException(NOT_EXIST_MEMBER));
    }

    private void isCorrectAccountPw(String accountPw) {
//        isAccountPwCombination(accountPw);
        if (MemberCondition.ACCOUNT_PW.isNotCorrect(accountPw)) {
            throw new IncorrectAccountPwException(ErrorEnum.INCORRECT_ACCOUNT_PW);
        }
    }

    private void isExistAuthEmail(String email, String authToken) {
        if (!emailAuthQueryRepository.isExistAuthEmail(email, authToken,
                REGISTER_EMAIL_AUTH_MINUTE.getDateTimeMinusMinutes(), EmailAuthProviderStatus.C)) {
            throw new EmailNotAuthenticatedException(ErrorEnum.EMAIL_NOT_AUTHENTICATED);
        }
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
            throw new NotExistMemberException(NOT_EXIST_MEMBER);
        }
    }

    private Member getMemberWithSearch(Long memberId) throws NotExistMemberException {
        Optional<Member> optionalMember = memberRepository.findOneByIdWithSearch(memberId);

        return optionalMember.orElseThrow(() -> new NotExistMemberException(NOT_EXIST_MEMBER));
    }

    private void isExistFollowedMember(Long memberId, Long followedMemberId) {
        if (memberQueryRepository.isExistFollowedMember(memberId, followedMemberId)) {
            throw new AlreadyFollowedMemberException(ErrorEnum.ALREADY_FOLLOWED_MEMBER);
        }
    }

    private void isBlockedOrBlockingAboutAll(Long memberId, Long targetMemberId) {
        if (memberQueryRepository.isBlockedOrBlockingAndStatus(memberId, targetMemberId, BlockedMemberStatus.A)) {
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

    private String getUrlByMultipartFile(MultipartFile multipartFile) throws IOException {
        return awsS3Service.upload(multipartFile, "member");
    }
}
