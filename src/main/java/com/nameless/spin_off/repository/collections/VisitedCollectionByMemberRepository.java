package com.nameless.spin_off.repository.collections;

import com.nameless.spin_off.entity.collections.VisitedCollectionByMember;
import com.nameless.spin_off.entity.post.VisitedPostByMember;
import org.springframework.data.jpa.repository.JpaRepository;

public interface VisitedCollectionByMemberRepository extends JpaRepository<VisitedCollectionByMember, Long> {
}
