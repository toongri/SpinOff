package com.nameless.spin_off.repository.collection;

import com.nameless.spin_off.entity.collection.Collection;
import com.nameless.spin_off.entity.member.Member;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface CollectionRepository extends JpaRepository<Collection, Long> {
    List<Collection> findAllByMember(Member member);

    @Query("SELECT DISTINCT collection FROM Collection collection " +
            "WHERE collection.id in :collectIds")
    List<Collection> findAllByIdIn(@Param("collectIds") List<Long> collectIds);

    @Query("SELECT DISTINCT collection FROM Collection collection " +
            "LEFT JOIN FETCH collection.likedCollections " +
            "WHERE collection.id = :id")
    Optional<Collection> findOneByIdWithLikedCollection(@Param("id") Long id);

    @Query("SELECT DISTINCT collection FROM Collection collection " +
            "LEFT JOIN FETCH collection.followingMembers " +
            "WHERE collection.id = :id")
    Optional<Collection> findOneByIdWithFollowingMember(@Param("id") Long id);

    @Query("SELECT DISTINCT collection FROM Collection collection " +
            "LEFT JOIN FETCH collection.viewedCollectionByIps viewedCollectionByIp " +
            "WHERE collection.id = :id")
    Optional<Collection> findOneByIdWithViewedByIp(@Param("id") Long id);

    @Query("SELECT DISTINCT collection FROM Collection collection " +
            "LEFT JOIN FETCH collection.commentInCollections comment " +
            "WHERE collection.id = :id")
    Optional<Collection> findOneByIdWithComment(@Param("id") Long id);

    @Query("SELECT DISTINCT collect FROM Collection collect " +
            "LEFT JOIN FETCH collect.collectedPosts collectPost " +
            "LEFT JOIN FETCH collectPost.post post " +
            "WHERE post.id = :postId")
    List<Collection> findAllByPostIdWithPost(@Param("postId") Long postId);

    @Query("SELECT DISTINCT collection FROM Collection collection " +
            "LEFT JOIN FETCH collection.member m " +
            "LEFT JOIN FETCH m.complains complain " +
            "WHERE collection.id = :id")
    Optional<Collection> findOneByIdWithComplainOfMember(@Param("id") Long id);

}
