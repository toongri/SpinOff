package com.nameless.spin_off.service.hashtag;

import com.nameless.spin_off.exception.hashtag.NotExistHashtagException;
import com.nameless.spin_off.exception.movie.NotExistMovieException;

public interface HashtagService {

    Long insertViewedHashtagByIp(String ip, Long hashtagId) throws NotExistHashtagException;
}
