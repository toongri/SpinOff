package com.nameless.spin_off.repository.movie;

import com.nameless.spin_off.entity.movie.FollowedMovie;
import org.springframework.data.jpa.repository.JpaRepository;

public interface FollowedMovieRepository extends JpaRepository<FollowedMovie, Long> {
}
