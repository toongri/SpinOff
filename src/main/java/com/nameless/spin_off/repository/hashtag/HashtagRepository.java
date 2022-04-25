package com.nameless.spin_off.repository.hashtag;

import com.nameless.spin_off.entity.hashtag.Hashtag;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface HashtagRepository extends JpaRepository<Hashtag, Long> {

    List<Hashtag> findAllByContentIn(List<String> contents);

    @Query("SELECT hashtag.id FROM Hashtag hashtag " +
            "WHERE hashtag.content in :contents")
    List<Long> findAllIdByContentIn(List<String> contents);

    @Query("SELECT followedHashtag.hashtag.id FROM FollowedHashtag followedHashtag " +
            "WHERE followedHashtag.member.id = :id")
    List<Long> findAllIdByFollowingMemberId(@Param("id")Long id);
}
