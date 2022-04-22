package com.nameless.spin_off.repository.query;

import com.nameless.spin_off.dto.MemberDto.MembersByContentDto;
import com.nameless.spin_off.dto.PostDto.*;
import com.nameless.spin_off.dto.*;
import com.nameless.spin_off.entity.enums.ContentsLengthEnum;
import com.nameless.spin_off.entity.enums.member.BlockedMemberStatus;
import com.nameless.spin_off.entity.enums.post.PublicOfPostStatus;
import com.nameless.spin_off.entity.hashtag.QPostedHashtag;
import com.nameless.spin_off.entity.member.QBlockedMember;
import com.nameless.spin_off.entity.post.Post;
import com.nameless.spin_off.repository.support.Querydsl4RepositorySupport;
import com.querydsl.core.types.dsl.BooleanExpression;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Slice;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static com.nameless.spin_off.entity.collection.QCollectedPost.collectedPost;
import static com.nameless.spin_off.entity.collection.QCollection.collection;
import static com.nameless.spin_off.entity.comment.QCommentInPost.commentInPost;
import static com.nameless.spin_off.entity.enums.post.PostPublicEnum.*;
import static com.nameless.spin_off.entity.hashtag.QFollowedHashtag.followedHashtag;
import static com.nameless.spin_off.entity.hashtag.QHashtag.hashtag;
import static com.nameless.spin_off.entity.hashtag.QPostedHashtag.postedHashtag;
import static com.nameless.spin_off.entity.member.QBlockedMember.blockedMember;
import static com.nameless.spin_off.entity.member.QFollowedMember.followedMember;
import static com.nameless.spin_off.entity.member.QMember.member;
import static com.nameless.spin_off.entity.movie.QFollowedMovie.followedMovie;
import static com.nameless.spin_off.entity.movie.QMovie.movie;
import static com.nameless.spin_off.entity.post.QLikedPost.likedPost;
import static com.nameless.spin_off.entity.post.QPost.post;
import static com.nameless.spin_off.entity.post.QViewedPostByIp.viewedPostByIp;

@Repository
public class PostQueryRepository extends Querydsl4RepositorySupport {

    public PostQueryRepository() {
        super(Post.class);
    }


    public List<MembersByContentDto> findAllLikeMemberByPostId(Long postId, List<Long> blockedMemberIds) {
        return getQueryFactory()
                .select(new QMemberDto_MembersByContentDto(
                        member.id, member.profileImg, member.nickname, member.accountId))
                .from(likedPost)
                .join(likedPost.member, member)
                .where(
                        memberNotIn(blockedMemberIds),
                        likedPost.post.id.eq(postId))
                .orderBy(likedPost.id.desc())
                .fetch();
    }

    public Slice<CollectedPostDto> findAllCollectedPostByCollectionId(Pageable pageable, Long collectionId,
                                                                      List<Long> blockedMemberIds, boolean isFollowing,
                                                                      boolean isAdmin) {
        return applySlicing(pageable, contentQuery -> contentQuery
                .select(new QPostDto_CollectedPostDto(
                        post.id, post.title, member.id, member.nickname, member.profileImg, post.thumbnailUrl))
                .from(post)
                .join(post.member, member)
                .join(post.collectedPosts, collectedPost)
                .on(collectedPost.collection.id.eq(collectionId))
                .where(
                        post.publicOfPostStatus.in(getPrivacyBound(isFollowing, isAdmin)),
                        memberNotIn(blockedMemberIds)));
    }

    public Slice<MyPagePostDto> findAllByMemberIdSliced(Long memberId, Pageable pageable,
                                                        boolean isFollowing, boolean isAdmin) {
        return applySlicing(pageable, contentQuery -> contentQuery
                .select(new QPostDto_MyPagePostDto(
                        post.id, post.title, post.thumbnailUrl))
                .from(post)
                .where(
                        post.publicOfPostStatus.in(getPrivacyBound(isFollowing, isAdmin)),
                        post.member.id.eq(memberId)));
    }

    public Optional<PublicOfPostStatus> findPublicByPostId(Long postId) {
        return Optional.ofNullable(getQueryFactory()
                .select(post.publicOfPostStatus)
                .from(post)
                .where(post.id.eq(postId))
                .fetchFirst());
    }

    public Optional<ThumbnailAndPublicPostDto> findThumbnailAndPublicByPostId(Long postId) {
        return Optional.ofNullable(getQueryFactory()
                .select(new QPostDto_ThumbnailAndPublicPostDto(post.thumbnailUrl, post.publicOfPostStatus))
                .from(post)
                .where(post.id.eq(postId))
                .fetchFirst());
    }

    public Optional<PostOwnerIdAndPublicPostDto> findOwnerIdAndPublicByPostId(Long postId) {
        return Optional.ofNullable(getQueryFactory()
                .select(new QPostDto_PostOwnerIdAndPublicPostDto(
                        post.member.id, post.publicOfPostStatus))
                .from(post)
                .where(post.id.eq(postId))
                .fetchFirst());
    }

    public Optional<PostIdAndPublicPostDto> findPostIdAndPublicByCommentId(Long commentId) {
        return Optional.ofNullable(getQueryFactory()
                .select(new QPostDto_PostIdAndPublicPostDto(
                        post.id, post.publicOfPostStatus))
                .from(commentInPost)
                .join(commentInPost.post, post)
                .where(commentInPost.id.eq(commentId))
                .fetchFirst());
    }

    public Boolean isBlockMembersPost(Long memberId, Long postId) {
        QBlockedMember blockingMember = new QBlockedMember("blockingMember");

        Integer fetchOne = getQueryFactory()
                .selectOne()
                .from(post)
                .join(post.member, member)
                .leftJoin(member.blockedMembers, blockedMember)
                .leftJoin(member.blockingMembers, blockingMember)
                .where(
                        post.id.eq(postId).and(
                                ((blockedMember.member.id.eq(memberId).and(
                                        blockedMember.blockedMemberStatus.eq(BlockedMemberStatus.A))).or(
                                        blockingMember.blockingMember.id.eq(memberId).and(
                                                blockingMember.blockedMemberStatus.eq(BlockedMemberStatus.A))))))
                .fetchFirst();

        return fetchOne != null;
    }

    public Boolean isFollowMembersOrOwnerPost(Long memberId, Long postId) {
        Integer fetchOne = getQueryFactory()
                .selectOne()
                .from(post)
                .join(post.member, member)
                .leftJoin(member.followingMembers, followedMember)
                .where(
                        post.id.eq(postId).and(
                                followedMember.followingMember.id.eq(memberId).or(
                                        member.id.eq(memberId))))
                .fetchFirst();

        return fetchOne != null;
    }

    public Optional<Long> findOwnerIdByPostId(Long postId) {
        return Optional.ofNullable(getQueryFactory()
                .select(post.member.id)
                .from(post)
                .where(
                        post.id.eq(postId))
                .fetchFirst());
    }

    public Boolean isExist(Long id) {
        Integer fetchOne = getQueryFactory()
                .selectOne()
                .from(post)
                .where(post.id.eq(id))
                .fetchFirst();

        return fetchOne != null;
    }

    public Boolean isExistCollectedPost(Long postId, Long collectionId) {
        Integer fetchOne = getQueryFactory()
                .selectOne()
                .from(collectedPost)
                .where(
                        post.id.eq(postId),
                        collection.id.eq(collectionId))
                .fetchFirst();

        return fetchOne != null;
    }

    public Boolean isExistLikedPost(Long memberId, Long postId) {
        Integer fetchOne = getQueryFactory()
                .selectOne()
                .from(likedPost)
                .where(
                        likedPost.member.id.eq(memberId),
                        likedPost.post.id.eq(postId))
                .fetchFirst();

        return fetchOne != null;
    }

    public Boolean isExistIp(Long id, String ip, LocalDateTime time) {
        Integer fetchOne = getQueryFactory()
                .selectOne()
                .from(viewedPostByIp)
                .where(
                        viewedPostByIp.ip.eq(ip),
                        viewedPostByIp.post.id.eq(id),
                        viewedPostByIp.createdDate.after(time))
                .fetchFirst();

        return fetchOne != null;
    }

    public Slice<SearchPageAtAllPostDto> findAllSlicedForSearchPageAtAll(
            String keyword, Pageable pageable, List<Long> blockedMemberIds) {
        return applySlicing(pageable, contentQuery -> contentQuery
                .select(new QPostDto_SearchPageAtAllPostDto(
                        post.id, post.title, member.id, member.nickname, member.profileImg, post.thumbnailUrl))
                .from(post)
                .join(post.member, member)
                .where(
                        post.publicOfPostStatus.in(DEFAULT_POST_PUBLIC.getPrivacyBound()),
                        post.title.contains(keyword),
                        memberNotIn(blockedMemberIds)));
    }

    public Slice<MainPagePostDto> findAllSlicedForMainPage(Pageable pageable, Long memberId, List<Long> blockedMemberIds) {
        List<Long> banList = new ArrayList<>(blockedMemberIds);
        addBanList(memberId, banList);

        return applySlicing(pageable, contentQuery -> contentQuery
                .select(new QPostDto_MainPagePostDto(
                        post.id,
                        post.title,
                        member.id,
                        member.nickname,
                        member.profileImg,
                        post.thumbnailUrl))
                .from(post)
                .join(post.member, member)
                .where(
                        post.publicOfPostStatus.in(DEFAULT_POST_PUBLIC.getPrivacyBound()),
                        member.id.notIn(banList)));
    }

    public Slice<RelatedPostDto> findAllRelatedPostByPostId(Pageable pageable,
                                                             List<Long> blockedMemberIds, Long postId) {
        QPostedHashtag relatedPostHashtag = new QPostedHashtag("relatedPostHashtag");

        return applySlicing(pageable, contentQuery -> contentQuery
                .select(new QPostDto_RelatedPostDto(
                        post.id,
                        post.title,
                        member.id,
                        member.nickname,
                        member.profileImg,
                        post.thumbnailUrl))
                .from(post)
                .join(post.member, member)
                .join(post.postedHashtags, postedHashtag)
                .join(postedHashtag.hashtag, hashtag)
                .join(hashtag.taggedPosts, relatedPostHashtag)
                .groupBy(post)
                .having(postedHashtag.count().goe(ContentsLengthEnum.RELATED_POST_MIN_TAG.getLength()))
                .where(
                        post.id.ne(postId),
                        relatedPostHashtag.post.id.eq(postId),
                        post.publicOfPostStatus.in(DEFAULT_POST_PUBLIC.getPrivacyBound()),
                        member.id.notIn(blockedMemberIds))
                .orderBy(postedHashtag.count().desc()));
    }

    public Slice<MainPagePostDto> findAllByFollowingMemberSlicedForMainPage(
            Pageable pageable, Long memberId) {

        return applySlicing(pageable, contentQuery -> contentQuery
                .select(new QPostDto_MainPagePostDto(
                        post.id, post.title, member.id, member.nickname, member.profileImg, post.thumbnailUrl))
                .from(post)
                .join(post.member, member)
                .join(member.followingMembers, followedMember)
                .where(
                        followedMember.followingMember.id.eq(memberId),
                        post.publicOfPostStatus.in(FOLLOW_POST_PUBLIC.getPrivacyBound())));
    }

    public Slice<MainPagePostDto> findAllByFollowedHashtagsSlicedForMainPage(
            Pageable pageable, List<Long> followedMovies, List<Long> followedMemberIds,
            List<Long> blockedMemberIds, Long memberId) {

        List<Long> banList = new ArrayList<>(blockedMemberIds);
        banList.addAll(followedMemberIds);

        return applySlicing(pageable, contentQuery -> contentQuery
                .selectDistinct(new QPostDto_MainPagePostDto(
                        post.id, post.title, member.id, member.nickname, member.profileImg, post.thumbnailUrl))
                .from(post)
                .join(post.member, member)
                .join(post.postedHashtags, postedHashtag)
                .join(postedHashtag.hashtag, hashtag)
                .join(hashtag.followingMembers, followedHashtag)
                .leftJoin(post.movie, movie)
                .where(
                        followedHashtag.member.id.eq(memberId),
                        postedMovieNotIn(followedMovies),
                        memberNotIn(banList),
                        post.publicOfPostStatus.in(DEFAULT_POST_PUBLIC.getPrivacyBound())));
    }

    public Slice<MainPagePostDto> findAllByFollowedMoviesSlicedForMainPage(
            Pageable pageable, List<Long> followedMemberIds, List<Long> blockedMemberIds, Long memberId) {
        List<Long> banList = new ArrayList<>(blockedMemberIds);
        banList.addAll(followedMemberIds);

        return applySlicing(pageable, contentQuery -> contentQuery
                .select(new QPostDto_MainPagePostDto(
                        post.id, post.title, member.id, member.nickname, member.profileImg, post.thumbnailUrl))
                .from(post)
                .join(post.member, member)
                .join(post.movie, movie)
                .join(movie.followingMembers, followedMovie)
                .where(
                        followedMovie.member.id.eq(memberId),
                        memberNotIn(banList),
                        post.publicOfPostStatus.in(DEFAULT_POST_PUBLIC.getPrivacyBound())));
    }

    public Optional<ReadPostDto> findByIdForRead(Long postId, List<Long> blockedMemberIds) {
        return Optional.ofNullable(getQueryFactory()
                .select(new QPostDto_ReadPostDto(
                        post.id, member.id, member.profileImg, member.nickname, member.accountId, post.title,
                        post.createdDate, post.lastModifiedDate, post.content, movie.thumbnail, movie.title,
                        movie.directorName, likedPost.countDistinct(), commentInPost.countDistinct(), post.publicOfPostStatus))
                .from(post)
                .join(post.member, member)
                .leftJoin(post.movie, movie)
                .leftJoin(post.likedPosts, likedPost)
                .on(likedPost.member.id.notIn(blockedMemberIds))
                .leftJoin(post.commentInPosts, commentInPost)
                .on(commentInPost.member.id.notIn(blockedMemberIds))
                .groupBy(post)
                .where(post.id.eq(postId))
                .fetchFirst());
    }

    public Optional<ReadPostDto> findByIdForRead(Long postId) {
        return Optional.ofNullable(getQueryFactory()
                .select(new QPostDto_ReadPostDto(
                        post.id, member.id, member.profileImg, member.nickname, member.accountId, post.title,
                        post.createdDate, post.lastModifiedDate, post.content, movie.thumbnail, movie.title,
                        movie.directorName, post.publicOfPostStatus))
                .from(post)
                .join(post.member, member)
                .leftJoin(post.movie, movie)
                .where(
                        post.id.eq(postId))
                .fetchFirst());
    }

    public Slice<SearchPageAtHashtagPostDto> findAllByHashtagsSlicedForSearchPage(
            Pageable pageable, List<Long> hashtagIds, List<Long> blockedMemberIds) {
        return applySlicing(pageable, contentQuery -> contentQuery
                .selectDistinct(new QPostDto_SearchPageAtHashtagPostDto(
                        post.id, post.title, member.id, member.nickname, member.profileImg, post.thumbnailUrl))
                .from(post)
                .join(post.member, member)
                .join(post.postedHashtags, postedHashtag)
                .where(postedHashtag.hashtag.id.in(hashtagIds),
                        memberNotIn(blockedMemberIds),
                        post.publicOfPostStatus.in(DEFAULT_POST_PUBLIC.getPrivacyBound())));
    }

    public List<Post> findAllByViewAfterTime(LocalDateTime time) {
        return getQueryFactory()
                .selectFrom(post)
                .join(post.viewedPostByIps, viewedPostByIp).fetchJoin()
                .where(
                        viewedPostByIp.createdDate.after(time))
                .fetch();
    }

    private void addBanList(Long memberId, List<Long> blockedMemberIds) {
        if (memberId != null) {
            blockedMemberIds.add(memberId);
        }
    }

    private List<PublicOfPostStatus> getPrivacyBound(boolean isFollowing, boolean isAdmin) {
        if (isAdmin) {
            return ADMIN_POST_PUBLIC.getPrivacyBound();
        } else if (isFollowing) {
            return FOLLOW_POST_PUBLIC.getPrivacyBound();
        } else {
            return DEFAULT_POST_PUBLIC.getPrivacyBound();
        }
    }

    private BooleanExpression commentMemberNotIn(List<Long> memberIds) {
        return memberIds.isEmpty() ? null : commentInPost.member.id.notIn(memberIds).or(commentInPost.isNull());
    }

    private BooleanExpression likedPostMemberNotIn(List<Long> memberIds) {
        return memberIds.isEmpty() ? null : likedPost.member.id.notIn(memberIds).or(likedPost.isNull());
    }
    private BooleanExpression memberNotIn(List<Long> members) {
        return members.isEmpty() ? null : member.id.notIn(members);
    }
    private BooleanExpression postedMovieNotIn(List<Long> movies) {
        return movies.isEmpty() ? null : post.movie.id.notIn(movies).or(post.movie.isNull());
    }
}
