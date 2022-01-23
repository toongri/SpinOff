package com.nameless.spin_off.repository.member;

import com.nameless.spin_off.entity.member.Member;
import org.springframework.data.jpa.repository.JpaRepository;

public interface MemberRepository extends JpaRepository<Member, Long> {
}
