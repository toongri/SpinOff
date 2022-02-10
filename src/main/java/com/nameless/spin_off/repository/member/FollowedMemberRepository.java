package com.nameless.spin_off.repository.member;

import com.nameless.spin_off.entity.member.FollowedMember;
import org.springframework.data.jpa.repository.JpaRepository;

public interface FollowedMemberRepository extends JpaRepository<FollowedMember, Long> {
}
