package com.nameless.spin_off.repository.collections;

import com.nameless.spin_off.entity.collections.Collection;
import com.nameless.spin_off.entity.member.Member;
import com.nameless.spin_off.entity.post.Post;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import javax.swing.text.html.Option;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

public interface CollectionRepository extends JpaRepository<Collection, Long> {
    List<Collection> findAllByMember(Member member);

    @Query("SELECT DISTINCT collection FROM Collection collection " +
            "LEFT JOIN FETCH collection.collectedPosts collectedposts " +
            "LEFT JOIN FETCH collectedposts.post post " +
            "WHERE collection.id in :collectIds AND collection.member.id = :memberId")
    List<Collection> findAllByIdInAndMemberIdWithPost(@Param("collectIds") List<Long> collectIds, @Param("memberId") Long memberId);

    @Query("SELECT DISTINCT collection FROM Collection collection " +
            "LEFT JOIN FETCH collection.likedCollections " +
            "WHERE collection.id = :id")
    Optional<Collection> findOneByIdWithLikedCollection(@Param("id") Long id);

    @Query("SELECT DISTINCT collection FROM Collection collection " +
            "LEFT JOIN FETCH collection.followedCollections " +
            "WHERE collection.id = :id")
    Optional<Collection> findOneByIdWithFollowedCollection(@Param("id") Long id);

    @Query("SELECT DISTINCT collection FROM Collection collection " +
            "LEFT JOIN FETCH collection.viewedCollectionByIps viewedCollectionByIp " +
            "WHERE collection.id = :id")
    Optional<Collection> findOneByIdWithViewedByIp(@Param("id") Long id);

    @Query("SELECT DISTINCT collection FROM Collection collection " +
            "LEFT JOIN FETCH collection.commentInCollections comment " +
            "WHERE collection.id = :id")
    Optional<Collection> findOneByIdWithComment(@Param("id") Long id);

    @Query("SELECT followedCollection.collection FROM FollowedCollection followedCollection " +
            "WHERE followedCollection.member.id = :id")
    List<Collection> findAllByFollowingMemberId(@Param("id") Long id);

    @Query("SELECT DISTINCT collect FROM Collection collect " +
            "LEFT JOIN FETCH collect.collectedPosts collectPost " +
            "LEFT JOIN FETCH collectPost.post post " +
            "WHERE post.id = :postId")
    List<Collection> findAllByPostIdWithPost(@Param("postId") Long postId);

    @Query("SELECT collection FROM Collection collection " +
            "LEFT JOIN FETCH collection.member m " +
            "LEFT JOIN FETCH m.complains complain " +
            "WHERE collection.id = :id")
    Optional<Collection> findOneByIdWithComplainOfMember(@Param("id") Long id);
}
