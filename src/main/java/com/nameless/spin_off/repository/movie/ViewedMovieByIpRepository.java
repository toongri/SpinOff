package com.nameless.spin_off.repository.movie;

import com.nameless.spin_off.entity.movie.ViewedMovieByIp;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ViewedMovieByIpRepository extends JpaRepository<ViewedMovieByIp, Long> {
}
