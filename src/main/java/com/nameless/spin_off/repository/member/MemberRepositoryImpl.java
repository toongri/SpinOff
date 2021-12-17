package com.nameless.spin_off.repository.member;

import com.nameless.spin_off.domain.member.Member;
import com.querydsl.jpa.impl.JPAQueryFactory;
import lombok.RequiredArgsConstructor;

import javax.persistence.EntityManager;
import java.util.List;

import static com.nameless.spin_off.domain.member.QMember.member;

@RequiredArgsConstructor
public class MemberRepositoryImpl implements MemberRepositoryCustom {

    private final EntityManager em;
    private final JPAQueryFactory jpaQueryFactory;


    @Override
    public void save(Member member) {
        em.persist(member);
    }

    @Override
    public Member findOne(Long id) {
        return em.find(Member.class, id);
    }

    @Override
    public List<Member> findAll() {

        List<Member> members = jpaQueryFactory
                .select(member)
                .from(member)
                .fetch();

        return members;

    }

    @Override
    public List<Member> findByName(String name) {

        List<Member> members = jpaQueryFactory
                .select(member)
                .from(member)
                .where(member.name.eq(name))
                .fetch();

        return members;
    }
}
