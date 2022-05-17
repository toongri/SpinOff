package com.nameless.spin_off.repository.member;

import com.nameless.spin_off.enums.member.BlockedMemberStatus;
import com.nameless.spin_off.entity.member.Member;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

public interface MemberRepository extends JpaRepository<Member, Long> {

    Optional<Member> findByNickname(String nickname);
    Optional<Member> findOneByAccountId(String accountId);
    Optional<Member> findOneByGoogleEmail(String googleEmail);
    Optional<Member> findOneByNaverEmail(String naverEmail);
    Optional<Member> findOneByKakaoEmail(String kakaoEmail);

    @Query("SELECT m FROM Member m " +
            "LEFT JOIN FETCH m.roles role " +
            "WHERE m.accountId = :accountId")
    Optional<Member> findByAccountIdWithRoles(@Param("accountId") String accountId);

    @Query("SELECT followedMember.member FROM FollowedMember followedMember " +
            "WHERE followedMember.followingMember.id = :id")
    List<Member> findAllByFollowingMemberId(@Param("id") Long id);

    @Query("SELECT DISTINCT m FROM Member m " +
            "LEFT JOIN FETCH m.followedMembers followedMember " +
            "WHERE m.id = :id")
    Optional<Member> findOneByIdWithFollowedMember(@Param("id") Long id);

    @Query("SELECT DISTINCT m FROM Member m " +
            "LEFT JOIN FETCH m.searches " +
            "WHERE m.id = :id")
    Optional<Member> findOneByIdWithSearch(@Param("id") Long id);

    @Query("SELECT followingMember.member.id FROM FollowedMember followingMember " +
            "WHERE followingMember.followingMember.id = :id")
    List<Long> findAllIdByFollowingMemberId(@Param("id")Long id);

    @Query("SELECT blockingMember.member.id FROM BlockedMember blockingMember " +
            "WHERE blockingMember.blockingMember.id = :id AND blockingMember.blockedMemberStatus = :status")
    List<Long> findAllIdByBlockingMemberId(@Param("id")Long id,
                                           @Param("status") BlockedMemberStatus status);

    @Query("SELECT blockedMember.blockingMember.id FROM BlockedMember blockedMember " +
            "WHERE blockedMember.member.id = :id AND blockedMember.blockedMemberStatus = :status")
    List<Long> findAllIdByBlockedMemberId(@Param("id")Long id,
                                           @Param("status") BlockedMemberStatus status);

    @Query("SELECT DISTINCT m.id FROM Member m " +
            "LEFT JOIN m.blockingMembers blockingM " +
            "LEFT JOIN m.blockedMembers blockedM " +
            "WHERE (blockingM.blockingMember.id = :id AND blockingM.blockedMemberStatus = :status) " +
            "OR (blockedM.member.id = :id AND blockedM.blockedMemberStatus = :status)")
    List<Long> findBlockingAllAndBlockedAllByIdAndBlockStatus(@Param("id") Long id,
                                                              @Param("status") BlockedMemberStatus status);

    @Query("SELECT m FROM Member m " +
            "JOIN FETCH m.followingMembers followingM " +
            "LEFT JOIN FETCH m.blockingMembers blockingM " +
            "LEFT JOIN FETCH m.complains complainM " +
            "WHERE (followingM.createdDate > :time)")
    List<Member> findAllByViewAfterTime(@Param("time") LocalDateTime time);
}
