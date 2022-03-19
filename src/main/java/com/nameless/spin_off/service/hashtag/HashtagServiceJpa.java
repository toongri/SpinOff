package com.nameless.spin_off.service.hashtag;

import com.nameless.spin_off.entity.enums.hashtag.HashtagScoreEnum;
import com.nameless.spin_off.entity.hashtag.FollowedHashtag;
import com.nameless.spin_off.entity.hashtag.Hashtag;
import com.nameless.spin_off.entity.hashtag.ViewedHashtagByIp;
import com.nameless.spin_off.entity.member.Member;
import com.nameless.spin_off.exception.hashtag.NotExistHashtagException;
import com.nameless.spin_off.exception.member.AlreadyFollowedHashtagException;
import com.nameless.spin_off.exception.member.NotExistMemberException;
import com.nameless.spin_off.repository.hashtag.FollowedHashtagRepository;
import com.nameless.spin_off.repository.hashtag.ViewedHashtagByIpRepository;
import com.nameless.spin_off.repository.query.HashtagQueryRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

import static com.nameless.spin_off.entity.enums.ContentsTimeEnum.VIEWED_BY_IP_MINUTE;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class HashtagServiceJpa implements HashtagService{
    private final HashtagQueryRepository hashtagQueryRepository;
    private final ViewedHashtagByIpRepository viewedHashtagByIpRepository;
    private final FollowedHashtagRepository followedHashtagRepository;

    @Transactional
    @Override
    public Long insertViewedHashtagByIp(String ip, Long hashtagId) throws NotExistHashtagException {

        isExistHashtag(hashtagId);
        if (!isExistHashtagIp(hashtagId, ip)) {
            return viewedHashtagByIpRepository.
                    save(ViewedHashtagByIp.createViewedHashtagByIp(ip, Hashtag.createHashtag(hashtagId))).getId();
        } else {
            return null;
        }
    }

    @Transactional
    @Override
    public Long insertFollowedHashtagByHashtagId(Long memberId, Long hashtagId) throws
            NotExistMemberException, NotExistHashtagException, AlreadyFollowedHashtagException {

        isExistHashtag(hashtagId);
        isExistFollowedHashtag(memberId, hashtagId);

        return followedHashtagRepository.save(FollowedHashtag
                .createFollowedHashtag(Member.createMember(memberId), Hashtag.createHashtag(hashtagId))).getId();
    }

    @Transactional
    @Override
    public int updateAllPopularity() {
        List<Hashtag> hashtags =
                hashtagQueryRepository.findAllByViewAfterTime(HashtagScoreEnum.HASHTAG_VIEW.getOldestDate());

        for (Hashtag hashtag : hashtags) {
            hashtag.updatePopularity();
        }

        return hashtags.size();
    }

    private void isExistFollowedHashtag(Long memberId, Long followedHashtagId) {
        if (hashtagQueryRepository.isExistFollowedHashtag(memberId, followedHashtagId)) {
            throw new AlreadyFollowedHashtagException();
        }
    }

    private void isExistHashtag(Long hashtagId) {
        if (!hashtagQueryRepository.isExist(hashtagId)) {
            throw new NotExistHashtagException();
        }
    }

    private boolean isExistHashtagIp(Long hashtagId, String ip) {
        return hashtagQueryRepository.isExistIp(hashtagId, ip, VIEWED_BY_IP_MINUTE.getDateTime());
    }
}
