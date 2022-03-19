package com.nameless.spin_off.service.hashtag;

import com.nameless.spin_off.exception.hashtag.NotExistHashtagException;
import com.nameless.spin_off.exception.member.AlreadyFollowedHashtagException;
import com.nameless.spin_off.exception.member.NotExistMemberException;

public interface HashtagService {

    Long insertViewedHashtagByIp(String ip, Long hashtagId) throws NotExistHashtagException;
    Long insertFollowedHashtagByHashtagId(Long memberId, Long hashtagId)
            throws NotExistMemberException, NotExistHashtagException, AlreadyFollowedHashtagException;

    int updateAllPopularity();

}
