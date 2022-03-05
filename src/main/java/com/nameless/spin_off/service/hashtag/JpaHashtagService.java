package com.nameless.spin_off.service.hashtag;

import com.nameless.spin_off.entity.hashtag.Hashtag;
import com.nameless.spin_off.entity.member.Member;
import com.nameless.spin_off.exception.hashtag.NotExistHashtagException;
import com.nameless.spin_off.exception.member.AlreadyFollowedHashtagException;
import com.nameless.spin_off.exception.member.NotExistMemberException;
import com.nameless.spin_off.repository.hashtag.HashtagRepository;
import com.nameless.spin_off.repository.member.MemberRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.Optional;

import static com.nameless.spin_off.entity.enums.hashtag.HashtagScoreEnum.HASHTAG_VIEW;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class JpaHashtagService implements HashtagService{

    private final HashtagRepository hashtagRepository;
    private final MemberRepository memberRepository;

    @Transactional()
    @Override
    public Long insertViewedHashtagByIp(String ip, Long hashtagId) throws NotExistHashtagException {

        Hashtag hashtag = getHashtagByIdWithViewedByIp(hashtagId);

        return hashtag.insertViewedHashtagByIp(ip);
    }

    @Transactional()
    @Override
    public Long insertFollowedHashtagByHashtagId(Long memberId, Long hashtagId) throws
            NotExistMemberException, NotExistHashtagException, AlreadyFollowedHashtagException {

        Member member = getMemberByIdWithHashtag(memberId);
        Hashtag hashtag = getHashtagByIdWithFollowingMember(hashtagId);

        return member.addFollowedHashtag(hashtag);
    }

    private Hashtag getHashtagByIdWithFollowingMember(Long hashtagId) throws NotExistHashtagException {
        Optional<Hashtag> optionalHashtag = hashtagRepository.findOneByIdWithFollowingMember(hashtagId);

        return optionalHashtag.orElseThrow(NotExistHashtagException::new);
    }

    private Member getMemberByIdWithHashtag(Long memberId) throws NotExistMemberException {
        Optional<Member> optionalMember = memberRepository.findOneByIdWithFollowedHashtag(memberId);

        return optionalMember.orElseThrow(NotExistMemberException::new);
    }


    public Hashtag getHashtagByIdWithViewedByIp(Long hashtagId) throws NotExistHashtagException {

        Optional<Hashtag> optionalHashtag = hashtagRepository
                .findOneByIdWithViewedByIp(hashtagId, LocalDateTime.now().minusDays(HASHTAG_VIEW.getLatestDay()));

        return optionalHashtag.orElseThrow(NotExistHashtagException::new);
    }
}
