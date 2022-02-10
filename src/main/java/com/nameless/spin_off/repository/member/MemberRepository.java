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
            "LEFT JOIN FETCH followedHashtag.hashtag hashtag " +
            "WHERE m.id = :id")
    Optional<Member> findMemberByIdIncludeHashtag(@Param("id") Long id);
}
