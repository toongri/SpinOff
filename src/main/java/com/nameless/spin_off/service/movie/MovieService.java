package com.nameless.spin_off.service.movie;

import com.nameless.spin_off.exception.movie.NotExistMovieException;

public interface MovieService {

    Long insertViewedMovieByIp(String ip, Long movieId) throws NotExistMovieException;
}
