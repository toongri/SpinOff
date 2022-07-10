package com.nameless.spin_off.repository.collection;

import com.nameless.spin_off.entity.collection.Collection;
import com.nameless.spin_off.entity.member.Member;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDateTime;
import java.util.List;

public interface CollectionRepository extends JpaRepository<Collection, Long> {
    List<Collection> findAllByMember(Member member);

    @Query("SELECT collection FROM Collection collection " +
            "WHERE collection.id in :collectIds")
    List<Collection> findAllByIdIn(@Param("collectIds") List<Long> collectIds);

    @Query("SELECT collection.id FROM Collection collection " +
            "WHERE collection.id in :collectIds AND collection.member.id = :memberId")
    List<Long> findAllIdByIdIn(@Param("collectIds") List<Long> collectIds, @Param("memberId") Long memberId);

    @Query("SELECT DISTINCT collect FROM Collection collect " +
            "LEFT JOIN FETCH collect.collectedPosts collectPost " +
            "LEFT JOIN FETCH collectPost.post post " +
            "WHERE post.id = :postId")
    List<Collection> findAllByPostIdWithPost(@Param("postId") Long postId);

    @Query("SELECT DISTINCT c FROM Collection c " +
            "JOIN FETCH c.viewedCollectionByIps view " +
            "WHERE (view.createdDate > :time)")
    List<Collection> findAllByViewAfterTime(@Param("time") LocalDateTime time);
}
