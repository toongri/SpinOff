package com.nameless.spin_off.service.movie;

import com.nameless.spin_off.entity.movie.Movie;
import com.nameless.spin_off.entity.movie.ViewedMovieByIp;
import com.nameless.spin_off.repository.movie.MovieRepository;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;

import javax.persistence.EntityManager;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.*;

//@Rollback(value = false)
@SpringBootTest
@Transactional
class JpaMovieServiceTest {

    @Autowired MovieService movieService;
    @Autowired MovieRepository movieRepository;
    @Autowired EntityManager em;

    @Test
    public void 영화_조회수_증가() throws Exception{

        //given
        Movie movie = movieRepository.save(Movie.createMovie(0L, "", ""));
        Movie movie2 = movieRepository.save(Movie.createMovie(1L, "", ""));
        Movie movie3 = movieRepository.save(Movie.createMovie(2L, "", ""));

        //when
        for (int i = 0; i < 10; i++) {
            movieService.insertViewedMovieByIp(""+i, movie.getId());
            movieService.insertViewedMovieByIp(""+i%2, movie2.getId());
            movieService.insertViewedMovieByIp(""+i%3, movie3.getId());
        }

        //then
        assertThat(movie.getViewCount()).isEqualTo(movie.getViewedMovieByIps().size());
        assertThat(movie.getViewCount()).isEqualTo(10);
        assertThat(movie2.getViewCount()).isEqualTo(movie2.getViewedMovieByIps().size());
        assertThat(movie2.getViewCount()).isEqualTo(2);
        assertThat(movie3.getViewCount()).isEqualTo(movie3.getViewedMovieByIps().size());
        assertThat(movie3.getViewCount()).isEqualTo(3);

        for (ViewedMovieByIp viewedMovieByIp : movie.getViewedMovieByIps()) {
            System.out.println("viewedMovieByIp.getId() = " + viewedMovieByIp.getId());
        }
    }
}