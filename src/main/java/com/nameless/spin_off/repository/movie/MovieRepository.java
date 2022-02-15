package com.nameless.spin_off.repository.movie;

import com.nameless.spin_off.entity.movie.Movie;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface MovieRepository extends JpaRepository<Movie, Long> {
    List<Movie> findTagsByIdIn(List<Long> ids);

    @Query("SELECT movie FROM Movie movie " +
            "LEFT JOIN FETCH movie.viewedMovieByIps viewedMovieByip " +
            "WHERE movie.id = :id")
    Optional<Movie> findOneByIdWithViewedByIp(@Param("id") Long id);
}
