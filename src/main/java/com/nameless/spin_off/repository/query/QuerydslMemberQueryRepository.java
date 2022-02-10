package com.nameless.spin_off.repository.query;

import com.nameless.spin_off.entity.member.FollowedMember;
import com.nameless.spin_off.entity.member.Member;
import com.nameless.spin_off.entity.member.QFollowedMember;
import com.nameless.spin_off.entity.member.QMember;
import com.querydsl.jpa.impl.JPAQueryFactory;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

import static com.nameless.spin_off.entity.member.QFollowedMember.followedMember;
import static com.nameless.spin_off.entity.member.QMember.member;

@Repository
@RequiredArgsConstructor
public class QuerydslMemberQueryRepository implements MemberQueryRepository{

    private final JPAQueryFactory jpaQueryFactory;

}
