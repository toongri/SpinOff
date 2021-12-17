package com.nameless.spin_off.repository.member;

import com.nameless.spin_off.domain.member.Member;

import java.util.List;

public interface MemberRepositoryCustom {

    public void save(Member member);
    public Member findOne(Long id);
    public List<Member> findAll();
    public List<Member> findByName(String name);

}
