package com.nameless.spin_off.service.movie;

import com.nameless.spin_off.exception.member.AlreadyFollowedMovieException;
import com.nameless.spin_off.exception.member.NotExistMemberException;
import com.nameless.spin_off.exception.movie.NotExistMovieException;

public interface MovieService {

    Long insertViewedMovieByIp(String ip, Long movieId) throws NotExistMovieException;
    Long insertFollowedMovieByMovieId(Long memberId, Long movieId) throws NotExistMemberException, NotExistMovieException, AlreadyFollowedMovieException;
}
