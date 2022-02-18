package com.nameless.spin_off.repository.member;

import com.nameless.spin_off.entity.collections.Collection;
import com.nameless.spin_off.entity.member.Member;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface MemberRepository extends JpaRepository<Member, Long> {

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


    @Query("SELECT followedMember.followingMember FROM FollowedMember followedMember " +
            "WHERE followedMember.member.id = :id")
    List<Member> findAllByFollowedMemberId(@Param("id") Long id);

    @Query("SELECT m FROM Member m " +
            "LEFT JOIN FETCH m.followedMembers followedMember " +
            "WHERE m.id = :id")
    Optional<Member> findOneByIdWithFollowedMember(@Param("id") Long id);

    @Query("SELECT m FROM Member m " +
            "LEFT JOIN FETCH m.blockedMembers blockingMember " +
            "WHERE m.id = :id")
    Optional<Member> findOneByIdWithBlockedMember(@Param("id") Long id);

    @Query("SELECT m FROM Member m " +
            "LEFT JOIN FETCH m.followingMembers followingMember " +
            "WHERE m.id = :id")
    Optional<Member> findOneByIdWithFollowingMember(@Param("id") Long id);

    @Query("SELECT m FROM Member m " +
            "LEFT JOIN FETCH m.blockingMembers blockedMember " +
            "WHERE m.id = :id")
    Optional<Member> findOneByIdWithBlockingMember(@Param("id") Long id);

    @Query("SELECT DISTINCT m FROM Member m " +
            "LEFT JOIN FETCH m.followedHashtags followedHashtag " +
            "LEFT JOIN FETCH m.blockedMembers blockedMember " +
            "WHERE m.id = :id")
    Optional<Member> findOneByIdWithFollowedHashtagAndBlockedMember(@Param("id") Long id);

    @Query("SELECT DISTINCT m FROM Member m " +
            "LEFT JOIN FETCH m.followedMovies followedMovie " +
            "LEFT JOIN FETCH m.blockedMembers blockedMember " +
            "WHERE m.id = :id")
    Optional<Member> findOneByIdWithFollowedMovieAndBlockedMember(@Param("id") Long id);

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

    List<Member> findAllByAccountIdOrNickname(String accountId, String nickname);
}
