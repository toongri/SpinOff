package com.nameless.spin_off.repository.hashtag;

import com.nameless.spin_off.entity.hashtag.Hashtag;
import com.nameless.spin_off.entity.movie.Movie;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface HashtagRepository extends JpaRepository<Hashtag, Long> {
    List<Hashtag> findAllByContentIn(List<String> contents);

    @Query("SELECT hashtag FROM Hashtag hashtag " +
            "LEFT JOIN FETCH hashtag.viewedHashtagByIps viewedHashtagByip " +
            "WHERE hashtag.id = :id")
    Optional<Hashtag> findOneByIdWithViewedByIp(@Param("id") Long id);

    @Query("SELECT hashtag FROM Hashtag hashtag " +
            "LEFT JOIN FETCH hashtag.followingMembers followingMember " +
            "WHERE hashtag.id = :id")
    Optional<Hashtag> findOneByIdWithFollowingMember(@Param("id") Long id);

}
