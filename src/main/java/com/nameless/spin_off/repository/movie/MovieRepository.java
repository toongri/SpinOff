package com.nameless.spin_off.repository.movie;

import com.nameless.spin_off.entity.movie.Movie;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

public interface MovieRepository extends JpaRepository<Movie, Long> {

    @Query("SELECT DISTINCT movie FROM Movie movie " +
            "LEFT JOIN FETCH movie.taggedPosts taggedPost " +
            "WHERE movie.id = :id")
    Optional<Movie> findOneByIdWithTaggedPost(@Param("id") Long id);

    @Query("SELECT followedMovie.movie.id FROM FollowedMovie followedMovie " +
            "WHERE followedMovie.member.id = :id")
    List<Long> findAllIdByFollowingMemberId(@Param("id")Long id);

    @Query("SELECT DISTINCT m FROM Movie m " +
            "JOIN FETCH m.viewedMovieByIps view " +
            "WHERE view.createdDate > :time")
    List<Movie> findAllByViewAfterTime(@Param("time") LocalDateTime time);

    @Query("SELECT m.id FROM Movie m " +
            "WHERE m.id in :ids")
    List<Long> findAllIdsById(@Param("ids") List<Long> ids);
}
