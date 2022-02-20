package com.nameless.spin_off.repository.member;

import com.nameless.spin_off.entity.member.SearchedByMember;
import org.springframework.data.jpa.repository.JpaRepository;

public interface SearchRepository extends JpaRepository<SearchedByMember, Long> {
}
