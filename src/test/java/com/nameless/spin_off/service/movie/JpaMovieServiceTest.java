package com.nameless.spin_off.service.movie;

import com.nameless.spin_off.entity.movie.Movie;
import com.nameless.spin_off.entity.movie.ViewedMovieByIp;
import com.nameless.spin_off.repository.movie.MovieRepository;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;

import javax.persistence.EntityManager;

import static org.assertj.core.api.Assertions.assertThat;

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
        assertThat(movie.getViewScore()).isEqualTo(movie.getViewedMovieByIps().size());
        assertThat(movie.getViewScore()).isEqualTo(10);
        assertThat(movie2.getViewScore()).isEqualTo(movie2.getViewedMovieByIps().size());
        assertThat(movie2.getViewScore()).isEqualTo(2);
        assertThat(movie3.getViewScore()).isEqualTo(movie3.getViewedMovieByIps().size());
        assertThat(movie3.getViewScore()).isEqualTo(3);

        for (ViewedMovieByIp viewedMovieByIp : movie.getViewedMovieByIps()) {
            System.out.println("viewedMovieByIp.getId() = " + viewedMovieByIp.getId());
        }
    }
}