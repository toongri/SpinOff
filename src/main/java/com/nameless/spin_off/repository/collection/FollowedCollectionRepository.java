package com.nameless.spin_off.repository.collection;

import com.nameless.spin_off.entity.collection.FollowedCollection;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface FollowedCollectionRepository extends JpaRepository<FollowedCollection, Long> {

    @Query("SELECT followedCollection.id FROM FollowedCollection followedCollection " +
            "JOIN followedCollection.collection collection " +
            "WHERE (followedCollection.member.id = :memberId AND collection.member.id = :followedMemberId) OR " +
            "(followedCollection.member.id = :followedMemberId AND collection.member.id = :memberId)")
    List<Long> findAllIdByFollowingMemberIdAndMemberId
            (@Param("memberId") Long memberId, @Param("followedMemberId") Long followedMemberId);
}
