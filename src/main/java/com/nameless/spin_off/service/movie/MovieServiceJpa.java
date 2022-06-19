package com.nameless.spin_off.service.movie;

import com.nameless.spin_off.config.movie.MovieApiService;
import com.nameless.spin_off.entity.member.Member;
import com.nameless.spin_off.entity.movie.FollowedMovie;
import com.nameless.spin_off.entity.movie.Movie;
import com.nameless.spin_off.entity.movie.ViewedMovieByIp;
import com.nameless.spin_off.enums.ErrorEnum;
import com.nameless.spin_off.enums.movie.MovieScoreEnum;
import com.nameless.spin_off.exception.member.AlreadyFollowedMovieException;
import com.nameless.spin_off.exception.member.NotExistMemberException;
import com.nameless.spin_off.exception.movie.NotExistMovieException;
import com.nameless.spin_off.repository.movie.FollowedMovieRepository;
import com.nameless.spin_off.repository.movie.MovieRepository;
import com.nameless.spin_off.repository.movie.ViewedMovieByIpRepository;
import com.nameless.spin_off.repository.query.MovieQueryRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

import static com.nameless.spin_off.enums.ContentsTimeEnum.VIEWED_BY_IP_MINUTE;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class MovieServiceJpa implements MovieService{

    private final MovieRepository movieRepository;
    private final MovieQueryRepository movieQueryRepository;
    private final ViewedMovieByIpRepository viewedMovieByIpRepository;
    private final FollowedMovieRepository followedMovieRepository;
    private final MovieApiService movieApiService;

    @Transactional
    @Override
    public Long insertViewedMovieByIp(String ip, Long movieId) throws NotExistMovieException {

        Movie movie = getMovieById(movieId);

        if (!isExistMovieIp(movieId, ip)) {
            return viewedMovieByIpRepository.
                    save(ViewedMovieByIp.createViewedMovieByIp(ip, movie)).getId();
        } else {
            return null;
        }
    }

    @Transactional
    @Override
    public Long insertFollowedMovieByMovieId(Long memberId, Long movieId) throws
            NotExistMemberException, NotExistMovieException, AlreadyFollowedMovieException {

        Movie movie = getMovieById(movieId);
        isExistFollowedMovie(memberId, movieId);

        return followedMovieRepository.save(FollowedMovie
                .createFollowedMovie(Member.createMember(memberId), movie)).getId();
    }

    @Transactional
    @Override
    public int updateAllPopularity() {
        List<Movie> movies = movieQueryRepository
                .findAllByViewAfterTime(MovieScoreEnum.MOVIE_VIEW.getOldestDate());
        for (Movie movie : movies) {
            movie.updatePopularity();
        }
        return movies.size();
    }

    @Transactional
    @Override
    public int createMoviesByKobis(int startPage, int size, boolean isBatch) {
        List<Movie> allNew = movieApiService.findAllNew(startPage, size, isBatch);
        int cnt = 0;
        for (Movie movie : allNew) {
            if (movieApiService.updateActorsMovie(movie, false)) {
                movieApiService.updateThumbnailAndUrlByMovie(movie);
                movie.updateLastModifiedDate();
                movieRepository.save(movie);
                cnt++;
            }
        }
        return cnt;
    }

    @Transactional
    @Override
    public int updateMovieByNaver() {
        List<Movie> movies = movieQueryRepository.findAllWithoutNaverInfoOrderByCreateDesc();
        int cnt = 0;
        for (Movie movie : movies) {
            if (movieApiService.updateThumbnailAndUrlByMovie(movie)) {
                cnt++;
                movie.updateLastModifiedDate();
            }
        }
        return cnt;
    }

    @Transactional
    @Override
    public int updateMovieActorByKobis(int page, int size) {
        List<Movie> movies = movieQueryRepository.findAllWithoutActorOrderByCreateDesc(page, size);
        int cnt = 0;
        for (Movie movie : movies) {
            if (movieApiService.updateActorsMovie(movie, false)) {
                cnt++;
                movie.updateLastModifiedDate();
            }
        }
        return cnt;
    }


    private Movie getMovieById(Long movieId) throws NotExistMovieException {
        Optional<Movie> optionalMovie = movieRepository.findById(movieId);

        return optionalMovie.orElseThrow(() -> new NotExistMovieException(ErrorEnum.NOT_EXIST_MOVIE));
    }

    private void isExistFollowedMovie(Long memberId, Long followedMovieId) {
        if (movieQueryRepository.isExistFollowedMovie(memberId, followedMovieId)) {
            throw new AlreadyFollowedMovieException(ErrorEnum.ALREADY_FOLLOWED_MOVIE);
        }
    }

    private void isExistMovie(Long movieId) {
        if (!movieQueryRepository.isExist(movieId)) {
            throw new NotExistMovieException(ErrorEnum.NOT_EXIST_MOVIE);
        }
    }

    private boolean isExistMovieIp(Long movieId, String ip) {
        return movieQueryRepository.isExistIp(movieId, ip, VIEWED_BY_IP_MINUTE.getDateTimeMinusMinutes());
    }
}
