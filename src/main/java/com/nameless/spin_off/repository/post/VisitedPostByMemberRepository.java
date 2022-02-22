package com.nameless.spin_off.repository.post;

import com.nameless.spin_off.entity.post.VisitedPostByMember;
import org.springframework.data.jpa.repository.JpaRepository;

public interface VisitedPostByMemberRepository extends JpaRepository<VisitedPostByMember, Long> {
}
