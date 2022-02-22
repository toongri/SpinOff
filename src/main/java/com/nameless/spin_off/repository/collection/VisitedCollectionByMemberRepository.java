package com.nameless.spin_off.repository.collection;

import com.nameless.spin_off.entity.collection.VisitedCollectionByMember;
import org.springframework.data.jpa.repository.JpaRepository;

public interface VisitedCollectionByMemberRepository extends JpaRepository<VisitedCollectionByMember, Long> {
}
