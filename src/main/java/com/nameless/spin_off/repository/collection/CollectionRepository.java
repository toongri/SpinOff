package com.nameless.spin_off.repository.collection;

import com.nameless.spin_off.entity.collection.Collection;
import com.nameless.spin_off.entity.member.Member;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface CollectionRepository extends JpaRepository<Collection, Long> {
    List<Collection> findAllByMember(Member member);

    @Query("SELECT collection FROM Collection collection " +
            "WHERE collection.id in :collectIds")
    List<Collection> findAllByIdIn(@Param("collectIds") List<Long> collectIds);

    @Query("SELECT DISTINCT collect FROM Collection collect " +
            "LEFT JOIN FETCH collect.collectedPosts collectPost " +
            "LEFT JOIN FETCH collectPost.post post " +
            "WHERE post.id = :postId")
    List<Collection> findAllByPostIdWithPost(@Param("postId") Long postId);
}
