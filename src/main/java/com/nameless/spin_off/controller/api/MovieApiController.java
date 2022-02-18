package com.nameless.spin_off.controller.api;

import com.nameless.spin_off.exception.hashtag.NotExistHashtagException;
import com.nameless.spin_off.exception.member.AlreadyFollowedHashtagException;
import com.nameless.spin_off.exception.member.AlreadyFollowedMovieException;
import com.nameless.spin_off.exception.member.NotExistMemberException;
import com.nameless.spin_off.exception.movie.NotExistMovieException;
import com.nameless.spin_off.service.movie.MovieService;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
@RequestMapping(value = "/api/movie")
public class MovieApiController {

    private final MovieService movieService;

    @PostMapping("/view")
    public MovieResult<Long> createViewOne(@RequestBody String ip, @RequestBody Long postId) throws NotExistMovieException {
        Long aLong = movieService.insertViewedMovieByIp(ip, postId);

        return new MovieResult<Long>(aLong);
    }

    @PostMapping("/follow")
    public MovieResult<Long> createFollowOne(@RequestParam Long memberId, @RequestParam Long hashtagId)
            throws NotExistMovieException, AlreadyFollowedMovieException, NotExistMemberException {
        Long likedHashtagId = movieService.insertFollowedMovieByMovieId(memberId, hashtagId);

        return new MovieResult<Long>(likedHashtagId);
    }

    @Data
    @AllArgsConstructor
    public static class MovieResult<T> {
        private T data;
    }
}
