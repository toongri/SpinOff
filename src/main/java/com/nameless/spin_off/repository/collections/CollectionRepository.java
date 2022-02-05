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
    List<Collection> findAllByIdIn(List<Long> ids);
    List<Collection> findAllByMember(Member member);

    @Query("SELECT DISTINCT collection FROM Collection collection LEFT JOIN FETCH collection.collectedPosts collectedposts " +
            "JOIN FETCH collectedposts.post post ORDER BY collection.id ASC, post.id ASC")
    List<Collection> findAllFetchJoinPostOrderByCollectionIdAndPostId();

    @Query("SELECT DISTINCT collection FROM Collection collection LEFT JOIN FETCH collection.likedCollections WHERE collection.id = :id")
    Optional<Collection> findOneByIdFetchJoinLikedCollection(@Param("id") Long id);

    @Query("SELECT DISTINCT collection FROM Collection collection LEFT JOIN FETCH collection.followedCollections " +
            "WHERE collection.id = :id")
    Optional<Collection> findOneByIdFetchJoinFollowedCollection(@Param("id") Long id);

    @Query("SELECT DISTINCT collection FROM Collection collection " +
            "JOIN FETCH collection.viewedCollectionByIps viewedCollectionByIp WHERE collection.id = :id " +
            "ORDER BY viewedCollectionByIp.id DESC")
    Optional<Collection> findOneByIdFetchJoinViewedCollectionByIpOrderByViewedIpId(@Param("id") Long id);

    @Query("SELECT DISTINCT collection FROM Collection collection " +
            "JOIN FETCH collection.collectedPosts collectedposts JOIN FETCH collectedposts.post post " +
            "WHERE collection.id = :id")
    Optional<Collection> findOneByIdFetchJoinPost(@Param("id") Long id);

}
