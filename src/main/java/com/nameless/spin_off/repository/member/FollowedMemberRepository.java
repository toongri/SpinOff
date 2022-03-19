package com.nameless.spin_off.repository.member;

import com.nameless.spin_off.entity.member.FollowedMember;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.Optional;

public interface FollowedMemberRepository extends JpaRepository<FollowedMember, Long> {

    @Query("SELECT followedMember FROM FollowedMember followedMember " +
            "WHERE followedMember.followingMember.id = :memberId AND followedMember.member.id = :followedMemberId")
    Optional<FollowedMember> findByFollowingMemberIdAndMemberId
            (@Param("memberId") Long memberId, @Param("followedMemberId") Long followedMemberId);
}
