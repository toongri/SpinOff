package com.nameless.spin_off.repository.query;

import com.nameless.spin_off.dto.MemberDto.SearchPageAtAllMemberDto;
import com.nameless.spin_off.dto.QMemberDto_SearchPageAtAllMemberDto;
import com.nameless.spin_off.entity.member.Member;
import com.nameless.spin_off.repository.support.Querydsl4RepositorySupport;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Slice;
import org.springframework.stereotype.Repository;

import static com.nameless.spin_off.entity.member.QMember.member;

@Repository
public class MemberQueryRepository extends Querydsl4RepositorySupport {

    public MemberQueryRepository() {
        super(Member.class);
    }

    public Slice<SearchPageAtAllMemberDto> findAllSlicedSearchPageAtAll(String keyword, Pageable pageable) {
        return applySlicing(pageable, contentQuery -> contentQuery
                .select(new QMemberDto_SearchPageAtAllMemberDto(
                        member.id, member.profileImg, member.nickname, member.accountId))
                .from(member)
                .where(member.nickname.contains(keyword)));
    }
}
