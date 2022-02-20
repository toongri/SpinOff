package com.nameless.spin_off.service.member;

import com.nameless.spin_off.dto.MemberDto.CreateMemberVO;
import com.nameless.spin_off.entity.collections.Collection;
import com.nameless.spin_off.entity.member.BlockedMemberStatus;
import com.nameless.spin_off.entity.member.Member;
import com.nameless.spin_off.entity.movie.Movie;
import com.nameless.spin_off.entity.hashtag.Hashtag;
import com.nameless.spin_off.exception.member.*;
import com.nameless.spin_off.exception.movie.NotExistMovieException;
import com.nameless.spin_off.exception.hashtag.NotExistHashtagException;
import com.nameless.spin_off.repository.collections.CollectionRepository;
import com.nameless.spin_off.repository.member.MemberRepository;
import com.nameless.spin_off.repository.movie.MovieRepository;
import com.nameless.spin_off.repository.hashtag.HashtagRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class JpaMemberService implements MemberService {

    private final MemberRepository memberRepository;
    private final CollectionRepository collectionRepository;
    private final HashtagRepository hashtagRepository;
    private final MovieRepository movieRepository;

    @Transactional()
    @Override
    public Long insertMemberByMemberVO(CreateMemberVO memberVO) throws AlreadyAccountIdException, AlreadyNicknameException {

        List<Member> memberList = memberRepository
                .findAllByAccountIdOrNickname(memberVO.getAccountId(), memberVO.getNickname());

        if (memberList.isEmpty()) {
            Member member = memberRepository.save(Member.createMemberByCreateVO(memberVO));
            collectionRepository.save(Collection.createDefaultCollection(member));
            return member.getId();
        } else {
            if (memberList.stream().noneMatch(member -> member.getAccountId().equals(memberVO.getAccountId()))) {
                throw new AlreadyNicknameException();
            } else {
                throw new AlreadyAccountIdException();
            }
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
}
