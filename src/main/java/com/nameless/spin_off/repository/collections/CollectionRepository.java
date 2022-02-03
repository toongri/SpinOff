package com.nameless.spin_off.repository.collections;

import com.nameless.spin_off.entity.collections.Collection;
import com.nameless.spin_off.entity.member.Member;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Collections;
import java.util.List;

public interface CollectionRepository extends JpaRepository<Collection, Long> {
    List<Collection> findCollectionsByIdIn(List<Long> ids);
    List<Collection> findCollectionsByMember(Member member);
}
