package com.nameless.spin_off.repository.query;

import com.nameless.spin_off.dto.MemberDto.SearchAllMemberDto;
import com.nameless.spin_off.dto.MemberDto.SearchMemberDto;
import com.nameless.spin_off.dto.QMemberDto_SearchAllMemberDto;
import com.nameless.spin_off.entity.member.BlockedMember;
import com.nameless.spin_off.entity.member.Member;
import com.nameless.spin_off.entity.member.QBlockedMember;
import com.nameless.spin_off.repository.support.Querydsl4RepositorySupport;
import com.querydsl.core.types.dsl.BooleanExpression;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Slice;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import static com.nameless.spin_off.entity.help.QComplain.complain;
import static com.nameless.spin_off.entity.member.QBlockedMember.blockedMember;
import static com.nameless.spin_off.entity.member.QFollowedMember.followedMember;
import static com.nameless.spin_off.entity.member.QMember.member;

@Repository
public class MemberQueryRepository extends Querydsl4RepositorySupport {

    public MemberQueryRepository() {
        super(Member.class);
    }

    public String findAccountIdByEmail(String email) {
        return getQueryFactory()
                .select(member.accountId)
                .from(member)
                .where(member.email.eq(email))
                .fetchFirst();
    }

    public Boolean isExist(Long id) {
        Integer fetchOne = getQueryFactory()
                .selectOne()
                .from(member)
                .where(member.id.eq(id))
                .fetchFirst();

        return fetchOne != null;
    }

    public List<Member> findAllByViewAfterTime(LocalDateTime time) {
        return getQueryFactory()
                .selectFrom(member)
                .join(member.followingMembers, followedMember).fetchJoin()
                .leftJoin(member.blockingMembers, blockedMember).fetchJoin()
                .leftJoin(member.complains, complain).fetchJoin()
                .where(
                        followedMember.createdDate.after(time))
                .fetch();
    }

    public Slice<SearchAllMemberDto> findAllSlicedForSearchPageAtAll(
            String keyword, Pageable pageable, List<Long> blockedMembers) {

        return applySlicing(pageable, contentQuery -> contentQuery
                .select(new QMemberDto_SearchAllMemberDto(
                        member.id, member.profileImg, member.nickname, member.accountId))
                .from(member)
                .where(member.nickname.contains(keyword),
                        memberNotIn(blockedMembers)));
    }

    public Slice<SearchMemberDto> findAllSlicedForSearchPageAtMember(
            String keyword, Pageable pageable, List<Long> followedMemberIds, List<Long> blockedMembers) {

        Slice<Member> content = applySlicing(pageable, contentQuery -> contentQuery
                .selectFrom(member)
                .where(member.nickname.contains(keyword),
                        memberNotIn(blockedMembers)));

        return MapContentToDtoForSearchPage(content, followedMemberIds);
    }

    public Boolean isExistFollowedMember(Long followingMemberId, Long followedMemberId) {
        Integer fetchOne = getQueryFactory()
                .selectOne()
                .from(followedMember)
                .where(
                        followedMember.followingMember.id.eq(followingMemberId),
                        followedMember.member.id.eq(followedMemberId))
                .fetchFirst();

        return fetchOne != null;
    }

    public Boolean isExistBlockedMember(Long blockingMemberId, Long blockedMemberId) {
        Integer fetchOne = getQueryFactory()
                .selectOne()
                .from(blockedMember)
                .where(
                        blockedMember.blockingMember.id.eq(blockingMemberId),
                        blockedMember.member.id.eq(blockedMemberId))
                .fetchFirst();

        return fetchOne != null;
    }

    public Optional<BlockedMember> findOneByBlockingIdAndBlockedId(Long blockingMemberId, Long blockedMemberId) {
        BlockedMember blockedMember = getQueryFactory()
                .select(QBlockedMember.blockedMember)
                .from(QBlockedMember.blockedMember)
                .where(
                        QBlockedMember.blockedMember.blockingMember.id.eq(blockingMemberId),
                        QBlockedMember.blockedMember.member.id.eq(blockedMemberId))
                .fetchFirst();

        return Optional.ofNullable(blockedMember);
    }

    private Slice<SearchMemberDto> MapContentToDtoForSearchPage(
            Slice<Member> contents, List<Long> followedMemberIds) {
        return contents.map(content -> new SearchMemberDto(content, followedMemberIds));
    }

    private BooleanExpression memberNotIn(List<Long> members) {
        return members.isEmpty() ? null : member.id.notIn(members);
    }
}
