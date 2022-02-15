package com.nameless.spin_off.service.movie;

import com.nameless.spin_off.entity.movie.Movie;
import com.nameless.spin_off.exception.movie.NotExistMovieException;
import com.nameless.spin_off.repository.movie.MovieRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class JpaMovieService implements MovieService{

    private final MovieRepository movieRepository;

    @Transactional(readOnly = false)
    @Override
    public Long insertViewedMovieByIp(String ip, Long movieId) throws NotExistMovieException {

        Movie movie = getMovieByIdWithViewedIp(movieId);

        movie.insertViewedMovieByIp(ip);

        return movie.getId();
    }

    private Movie getMovieByIdWithViewedIp(Long movieId) throws NotExistMovieException {
        Optional<Movie> optionalMovie = movieRepository.findOneByIdWithViewedByIp(movieId);

        return optionalMovie.orElseThrow(NotExistMovieException::new);
    }
}
