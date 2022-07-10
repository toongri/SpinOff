package com.nameless.spin_off.repository.query;

import com.nameless.spin_off.dto.*;
import com.nameless.spin_off.dto.MemberDto.*;
import com.nameless.spin_off.dto.PostDto.ThumbnailMemberDto;
import com.nameless.spin_off.entity.member.BlockedMember;
import com.nameless.spin_off.entity.member.Member;
import com.nameless.spin_off.entity.member.QFollowedMember;
import com.nameless.spin_off.enums.member.BlockedMemberStatus;
import com.nameless.spin_off.repository.support.Querydsl4RepositorySupport;
import com.querydsl.core.types.dsl.BooleanExpression;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Slice;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

import static com.nameless.spin_off.entity.help.QComplain.complain;
import static com.nameless.spin_off.entity.member.QBlockedMember.blockedMember;
import static com.nameless.spin_off.entity.member.QFollowedMember.followedMember;
import static com.nameless.spin_off.entity.member.QMember.member;
import static com.nameless.spin_off.entity.post.QPost.post;

@Repository
public class MemberQueryRepository extends Querydsl4RepositorySupport {

    public MemberQueryRepository() {
        super(Member.class);
    }

    public Optional<Member> findOneByIdAndDeleteDateNotNull(Long memberId) {
        return Optional.ofNullable(getQueryFactory()
                .select(member)
                .from(member)
                .where(
                        member.id.eq(memberId),
                        member.deleteDate.isNotNull())
                .fetchFirst());
    }

    public List<BlockedMemberDto> findAllBlockedMemberByMemberId(Long memberId) {
        return getQueryFactory()
                .select(new QMemberDto_BlockedMemberDto(
                        member.id, member.accountId, member.nickname, blockedMember.blockedMemberStatus))
                .from(blockedMember)
                .join(blockedMember.member, member)
                .where(blockedMember.blockingMember.id.eq(memberId))
                .orderBy(blockedMember.id.desc())
                .fetch();
    }

    public List<MembersByContentDto> findAllFollowedMemberByMemberId(Long memberId, List<Long> blockedMemberIds) {
        return getQueryFactory()
                .select(new QMemberDto_MembersByContentDto(
                        member.id, member.profileImg, member.nickname, member.accountId))
                .from(followedMember)
                .join(followedMember.member, member)
                .where(
                        memberNotIn(blockedMemberIds),
                        followedMember.followingMember.id.eq(memberId))
                .orderBy(followedMember.id.desc())
                .fetch();
    }

    public List<MembersByContentDto> findAllFollowingMemberByMemberId(Long memberId, List<Long> blockedMemberIds) {
        return getQueryFactory()
                .select(new QMemberDto_MembersByContentDto(
                        member.id, member.profileImg, member.nickname, member.accountId))
                .from(followedMember)
                .join(followedMember.followingMember, member)
                .where(
                        memberNotIn(blockedMemberIds),
                        followedMember.member.id.eq(memberId))
                .orderBy(followedMember.id.desc())
                .fetch();
    }

    public Optional<ReadMemberDto> findByIdForRead(Long memberId, List<Long> blockedMembers) {
        QFollowedMember followingMember = new QFollowedMember("followingMember");

        return Optional.ofNullable(getQueryFactory()
                .select(new QMemberDto_ReadMemberDto(
                        member.id, member.nickname, member.accountId, member.profileImg, member.bio,
                        post.countDistinct(), followedMember.countDistinct(), followingMember.countDistinct()))
                .from(member)
                .leftJoin(member.posts, post)
                .leftJoin(member.followingMembers, followedMember)
                .on(followedMember.followingMember.id.notIn(blockedMembers))
                .leftJoin(member.followedMembers, followingMember)
                .on(followingMember.member.id.notIn(blockedMembers))
                .groupBy(member)
                .where(member.id.eq(memberId))
                .fetchFirst());
    }

    public Optional<String> findAccountIdByEmail(String email) {
        return Optional.ofNullable(getQueryFactory()
                .select(member.accountId)
                .from(member)
                .where(member.email.eq(email))
                .fetchFirst());
    }

    public Boolean isExist(Long id) {
        Integer fetchOne = getQueryFactory()
                .selectOne()
                .from(member)
                .where(member.id.eq(id))
                .fetchFirst();

        return fetchOne != null;
    }

    public Boolean isBlockedOrBlockingAndStatus(Long memberId, Long targetMemberId, BlockedMemberStatus status) {
        Integer fetchOne = getQueryFactory()
                .selectOne()
                .from(blockedMember)
                .where(
                        blockedMember.blockedMemberStatus.eq(status),
                        (blockedMember.member.id.eq(memberId).and(
                                blockedMember.blockingMember.id.eq(targetMemberId))).or(
                                blockedMember.blockingMember.id.eq(memberId).and(
                                        blockedMember.member.id.eq(targetMemberId))))
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

    public List<Member> findAllByViewAfterTime(LocalDateTime time) {
        return getQueryFactory()
                .selectFrom(member).distinct()
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
            String keyword, Pageable pageable, Long memberId, List<Long> blockedMemberIds) {

        Slice<SearchMemberDto> content = applySlicing(pageable, contentQuery -> contentQuery
                .select(new QMemberDto_SearchMemberDto(
                        member.id, member.profileImg, member.nickname, member.accountId, member.bio,
                        followedMember.count()))
                .from(member)
                .leftJoin(member.followingMembers, followedMember)
                .on(followedMember.followingMember.id.notIn(blockedMemberIds))
                .groupBy(member)
                .where(
                        member.nickname.contains(keyword),
                        memberNotIn(blockedMemberIds)));

        if (memberId != null) {
            List<Long> memberIds = getMemberIds(content.getContent());

            Map<Long, List<FollowingMemberMemberDto>> followingMembersAtMember =
                    findAllFollowingMemberAtMember(memberId, memberIds);

            Map<Long, List<ThumbnailMemberDto>> postsAtMember = findAllPostAtMember(memberIds);

            content.getContent().forEach(o -> o.setFollowingMemberAndThumbnails(
                    followingMembersAtMember.get(o.getMemberId()),
                    postsAtMember.get(o.getMemberId())));
        }

        return content;
    }

    private Map<Long, List<ThumbnailMemberDto>> findAllPostAtMember(List<Long> memberIds) {
        return getQueryFactory()
                .select(new QPostDto_ThumbnailMemberDto(
                        post.member.id, post.thumbnailUrl))
                .from(post)
                .where(
                        post.member.id.in(memberIds),
                        post.thumbnailUrl.isNotNull())
                .fetch().stream().collect(Collectors.groupingBy(ThumbnailMemberDto::getMemberId));
    }

    private Map<Long, List<FollowingMemberMemberDto>> findAllFollowingMemberAtMember(Long memberId, List<Long> memberIds) {
        QFollowedMember ownerFollowedMember = new QFollowedMember("ownerFollowedMember");
        return getQueryFactory()
                .select(new QMemberDto_FollowingMemberMemberDto(
                        followedMember.member.id, member.id, member.popularity, member.nickname))
                .from(followedMember)
                .join(followedMember.followingMember, member)
                .join(member.followingMembers, ownerFollowedMember)
                .where(
                        followedMember.member.id.in(memberIds),
                        ownerFollowedMember.followingMember.id.eq(memberId))
                .fetch().stream().collect(Collectors.groupingBy(MemberDto.FollowingMemberMemberDto::getMemberId));
    }

    public Boolean isExistBlockingMember(Long blockingMemberId, Long blockedMemberId, BlockedMemberStatus status) {
        Integer fetchOne = getQueryFactory()
                .selectOne()
                .from(blockedMember)
                .where(
                        blockedMember.blockingMember.id.eq(blockingMemberId),
                        blockedMember.member.id.eq(blockedMemberId),
                        blockedMember.blockedMemberStatus.eq(status))
                .fetchFirst();

        return fetchOne != null;
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

    public Optional<BlockedMember> findOneByBlockingIdAndBlockedId(Long blockingMemberId, Long blockedMemberId) {

        return Optional.ofNullable(getQueryFactory()
                .select(blockedMember)
                .from(blockedMember)
                .where(
                        blockedMember.blockingMember.id.eq(blockingMemberId),
                        blockedMember.member.id.eq(blockedMemberId))
                .fetchFirst());
    }

    private List<Long> getMemberIds(List<SearchMemberDto> content) {
        return content.stream().map(SearchMemberDto::getMemberId).collect(Collectors.toList());
    }

    private BooleanExpression followingMemberNotIn(List<Long> members) {
        return members.isEmpty() ? null : followedMember.followingMember.id.notIn(members).or(followedMember.isNull());
    }

    private BooleanExpression followedMemberNotIn(List<Long> members, QFollowedMember followingMember) {
        return members.isEmpty() ? null : followingMember.member.id.notIn(members).or(followingMember.isNull());
    }

    private BooleanExpression memberNotIn(List<Long> members) {
        return members.isEmpty() ? null : member.id.notIn(members);
    }
}
