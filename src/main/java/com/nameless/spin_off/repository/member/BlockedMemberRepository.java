package com.nameless.spin_off.repository.member;

import com.nameless.spin_off.entity.member.BlockedMember;
import org.springframework.data.jpa.repository.JpaRepository;

public interface BlockedMemberRepository extends JpaRepository<BlockedMember, Long> {
}
