package com.nameless.spin_off.repository.member;

import com.nameless.spin_off.entity.member.Member;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface MemberRepository extends JpaRepository<Member, Long> {

    Optional<Member> findByNickname(String nickname);
    Optional<Member> findOneByAccountId(String accountId);

    @Query("SELECT m FROM Member m " +
            "LEFT JOIN FETCH m.roles role " +
            "WHERE m.googleEmail = :googleEmail")
    Optional<Member> findByGoogleEmailWithRoles(@Param("googleEmail") String googleEmail);

    @Query("SELECT m FROM Member m " +
            "LEFT JOIN FETCH m.roles role " +
            "WHERE m.naverEmail = :naverEmail")
    Optional<Member> findByNaverEmailWithRoles(@Param("naverEmail") String naverEmail);


    @Query("SELECT m FROM Member m " +
            "LEFT JOIN FETCH m.roles role " +
            "WHERE m.email = :email")
    Optional<Member> findByEmailWithRoles(@Param("email") String email);

    @Query("SELECT m FROM Member m " +
            "LEFT JOIN FETCH m.roles role " +
            "WHERE m.kakaoEmail = :kakaoEmail")
    Optional<Member> findByKakaoEmailWithRoles(@Param("kakaoEmail") String kakaoEmail);

    @Query("SELECT m FROM Member m " +
            "LEFT JOIN FETCH m.roles role " +
            "WHERE m.accountId = :accountId")
    Optional<Member> findByAccountIdWithRoles(@Param("accountId") String accountId);

    @Query("SELECT DISTINCT m FROM Member m " +
            "LEFT JOIN FETCH m.followedHashtags followedHashtag " +
            "WHERE m.id = :id")
    Optional<Member> findOneByIdWithFollowedHashtag(@Param("id") Long id);

    @Query("SELECT DISTINCT m FROM Member m " +
            "LEFT JOIN FETCH m.followedMovies followedMovie " +
            "WHERE m.id = :id")
    Optional<Member> findOneByIdWithFollowedMovie(@Param("id") Long id);

    @Query("SELECT followedMember.member FROM FollowedMember followedMember " +
            "WHERE followedMember.followingMember.id = :id")
    List<Member> findAllByFollowingMemberId(@Param("id") Long id);

    @Query("SELECT DISTINCT m FROM Member m " +
            "LEFT JOIN FETCH m.followedMembers followedMember " +
            "WHERE m.id = :id")
    Optional<Member> findOneByIdWithFollowedMember(@Param("id") Long id);

    @Query("SELECT DISTINCT m FROM Member m " +
            "LEFT JOIN FETCH m.blockedMembers blockedMember " +
            "WHERE m.id = :id")
    Optional<Member> findOneByIdWithBlockedMember(@Param("id") Long id);

    @Query("SELECT DISTINCT m FROM Member m " +
            "LEFT JOIN FETCH m.followingMembers followingMember " +
            "WHERE m.id = :id")
    Optional<Member> findOneByIdWithFollowingMember(@Param("id") Long id);

    @Query("SELECT DISTINCT m FROM Member m " +
            "LEFT JOIN FETCH m.blockingMembers blockingMember " +
            "WHERE m.id = :id")
    Optional<Member> findOneByIdWithBlockingMember(@Param("id") Long id);

    @Query("SELECT DISTINCT m FROM Member m " +
            "LEFT JOIN FETCH m.followedHashtags followedHashtag " +
            "LEFT JOIN FETCH m.followedMembers followedMember " +
            "LEFT JOIN FETCH m.followedMovies followedMovie " +
            "LEFT JOIN FETCH m.followedCollections followedCollection " +
            "LEFT JOIN FETCH m.blockedMembers blockedMember " +
            "WHERE m.id = :id")
    Optional<Member> findOneByIdWithFollowedContentsAndBlockedMember(@Param("id") Long id);

    @Query("SELECT DISTINCT m FROM Member m " +
            "LEFT JOIN FETCH m.followedMovies followedMovie " +
            "LEFT JOIN FETCH m.followedMembers followedMember " +
            "LEFT JOIN FETCH m.blockedMembers blockedMember " +
            "WHERE m.id = :id")
    Optional<Member> findOneByIdWithFollowedMovieAndBlockedAndFollowedMember(@Param("id") Long id);

    @Query("SELECT DISTINCT m FROM Member m " +
            "LEFT JOIN FETCH m.followedMembers followedMember " +
            "LEFT JOIN FETCH m.blockedMembers blockedMember " +
            "WHERE m.id = :id")
    Optional<Member> findOneByIdWithFollowedMemberAndBlockedMember(@Param("id") Long id);

    @Query("SELECT DISTINCT m FROM Member m " +
            "LEFT JOIN FETCH m.followedCollections followedCollection " +
            "LEFT JOIN FETCH m.blockedMembers blockedMember " +
            "WHERE m.id = :id")
    Optional<Member> findOneByIdWithFollowedCollectionAndBlockedMember(@Param("id") Long id);

    List<Member> findAllByAccountIdOrNicknameOrEmail(String accountId, String nickname, String email);

    @Query("SELECT DISTINCT m FROM Member m " +
            "LEFT JOIN FETCH m.searches " +
            "WHERE m.id = :id")
    Optional<Member> findOneByIdWithSearch(@Param("id") Long id);

    Optional<Member> findOneByEmail(String email);

    @Query("SELECT m FROM Member m " +
            "LEFT JOIN FETCH m.blockingMembers blockingMembers " +
            "WHERE blockingMembers.blockingMember.id = :id")
    List<Member> findAllByBlockingMemberId(@Param("id") Long id);
}
