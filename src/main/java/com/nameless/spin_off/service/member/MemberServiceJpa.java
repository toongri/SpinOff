package com.nameless.spin_off.service.member;

import com.nameless.spin_off.config.jwt.JwtTokenProvider;
import com.nameless.spin_off.dto.MemberDto;
import com.nameless.spin_off.dto.MemberDto.MemberLoginResponseDto;
import com.nameless.spin_off.dto.MemberDto.MemberRegisterRequestDto;
import com.nameless.spin_off.dto.MemberDto.MemberRegisterResponseDto;
import com.nameless.spin_off.entity.collection.Collection;
import com.nameless.spin_off.entity.enums.member.BlockedMemberStatus;
import com.nameless.spin_off.entity.enums.member.SearchedByMemberStatus;
import com.nameless.spin_off.entity.member.Member;
import com.nameless.spin_off.exception.member.*;
import com.nameless.spin_off.repository.collection.CollectionRepository;
import com.nameless.spin_off.repository.member.MemberRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class MemberServiceJpa implements MemberService {

    private final MemberRepository memberRepository;
    private final CollectionRepository collectionRepository;
    private final JwtTokenProvider jwtTokenProvider;
    private final PasswordEncoder passwordEncoder;

    @Transactional()
    @Override
    public Long insertMemberByMemberVO(MemberRegisterRequestDto memberVO) throws AlreadyAccountIdException, AlreadyNicknameException {

        validateDuplicate(memberVO.getAccountId(), memberVO.getNickname());

        Member member = memberRepository.save(Member.createMemberByCreateVO(memberVO));
        collectionRepository.save(Collection.createDefaultCollection(member));
        return member.getId();
    }

    @Transactional()
    @Override
    public MemberRegisterResponseDto registerMember(MemberRegisterRequestDto requestDto)
            throws AlreadyAccountIdException, AlreadyNicknameException {
        validateDuplicate(requestDto.getAccountId(), requestDto.getNickname());

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

    @Override
    public MemberLoginResponseDto loginMember(MemberDto.MemberLoginRequestDto requestDto) {
        Member member =
                memberRepository.findByAccountId(requestDto.getAccountId()).orElseThrow(LoginFailureException::new);

        if (!passwordEncoder.matches(requestDto.getAccountPw(), member.getAccountPw())) {
            throw new LoginFailureException();
        } else {
            return new MemberLoginResponseDto(member.getId(), jwtTokenProvider.createToken(requestDto.getAccountId()));
        }

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

    private void validateDuplicate(String accountId, String nickname) {

        List<Member> memberList = memberRepository
                .findAllByAccountIdOrNickname(accountId, nickname);
        if (memberList.stream().noneMatch(member -> member.getAccountId().equals(accountId))) {
            throw new AlreadyNicknameException();
        } else {
            throw new AlreadyAccountIdException();
        }
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
