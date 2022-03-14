package com.nameless.spin_off.controller.api;

import com.nameless.spin_off.config.auth.LoginMemberId;
import com.nameless.spin_off.exception.member.AlreadyFollowedMovieException;
import com.nameless.spin_off.exception.member.NotExistMemberException;
import com.nameless.spin_off.exception.movie.NotExistMovieException;
import com.nameless.spin_off.service.movie.MovieService;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@Slf4j
@RestController
@RequiredArgsConstructor
@RequestMapping(value = "/api/movie")
public class MovieApiController {

    private final MovieService movieService;

    @PostMapping("/{movieId}/view/{ip}")
    public MovieResult<Long> createViewOne(
            @PathVariable String ip, @PathVariable Long movieId) throws NotExistMovieException {

        log.info("createViewOne");
        log.info("movieId : {}", movieId);
        log.info("ip : {}", ip);

        return getResult(movieService.insertViewedMovieByIp(ip, movieId));
    }

    @PostMapping("/{movieId}/follow")
    public MovieResult<Long> createFollowOne(
            @LoginMemberId Long memberId, @PathVariable Long movieId)
            throws NotExistMovieException, AlreadyFollowedMovieException, NotExistMemberException {

        log.info("createFollowOne");
        log.info("memberId : {}", memberId);
        log.info("movieId : {}", movieId);

        return getResult(movieService.insertFollowedMovieByMovieId(memberId, movieId));
    }

    @Data
    @AllArgsConstructor
    public static class MovieResult<T> {
        private T data;
        Boolean isSuccess;
        String code;
        String message;
    }
    public <T> MovieResult<T> getResult(T data) {
        return new MovieResult<>(data, true, "0", "성공");
    }
}
