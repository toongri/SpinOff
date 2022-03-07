package com.nameless.spin_off.repository.query;

import com.nameless.spin_off.dto.MemberDto;
import com.nameless.spin_off.dto.MemberDto.SearchMemberDto;
import com.nameless.spin_off.dto.QMemberDto_SearchAllMemberDto;
import com.nameless.spin_off.entity.member.Member;
import com.nameless.spin_off.repository.support.Querydsl4RepositorySupport;
import com.querydsl.core.types.dsl.BooleanExpression;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Slice;
import org.springframework.stereotype.Repository;

import java.util.List;

import static com.nameless.spin_off.entity.member.QMember.member;

@Repository
public class MemberQueryRepository extends Querydsl4RepositorySupport {

    public MemberQueryRepository() {
        super(Member.class);
    }

    public Slice<MemberDto.SearchAllMemberDto> findAllSlicedForSearchPageAtAll(
            String keyword, Pageable pageable, List<Member> blockedMembers) {
        return applySlicing(pageable, contentQuery -> contentQuery
                .select(new QMemberDto_SearchAllMemberDto(
                        member.id, member.profileImg, member.nickname, member.accountId))
                .from(member)
                .where(member.nickname.contains(keyword),
                        memberNotIn(blockedMembers)));
    }

    public Slice<SearchMemberDto> findAllSlicedForSearchPageAtMember(
            String keyword, Pageable pageable, List<Member> followedMembers, List<Member> blockedMembers) {

        Slice<Member> content = applySlicing(pageable, contentQuery -> contentQuery
                .selectFrom(member)
                .where(member.nickname.contains(keyword),
                        memberNotIn(blockedMembers)));

        return MapContentToDtoForSearchPage(content, followedMembers);
    }

    private Slice<SearchMemberDto> MapContentToDtoForSearchPage(
            Slice<Member> contents, List<Member> followedMembers) {
        if (followedMembers.isEmpty()) {
            return contents.map(SearchMemberDto::new);
        } else {
            return contents.map(content -> new SearchMemberDto(content, followedMembers));
        }
    }

    private BooleanExpression memberNotIn(List<Member> members) {
        return members.isEmpty() ? null : member.notIn(members);
    }
}
