package com.nameless.spin_off.repository.query;

import com.nameless.spin_off.dto.PostDto;
import com.nameless.spin_off.dto.PostDto.IdAndPublicPostDto;
import com.nameless.spin_off.dto.PostDto.MainPagePostDto;
import com.nameless.spin_off.dto.PostDto.SearchPageAtAllPostDto;
import com.nameless.spin_off.dto.PostDto.SearchPageAtHashtagPostDto;
import com.nameless.spin_off.dto.QPostDto_MainPagePostDto;
import com.nameless.spin_off.dto.QPostDto_SearchPageAtAllPostDto;
import com.nameless.spin_off.dto.QPostDto_SearchPageAtHashtagPostDto;
import com.nameless.spin_off.entity.enums.member.BlockedMemberStatus;
import com.nameless.spin_off.entity.enums.post.PublicOfPostStatus;
import com.nameless.spin_off.entity.hashtag.Hashtag;
import com.nameless.spin_off.entity.member.QBlockedMember;
import com.nameless.spin_off.entity.movie.Movie;
import com.nameless.spin_off.entity.post.Post;
import com.nameless.spin_off.repository.support.Querydsl4RepositorySupport;
import com.querydsl.core.Tuple;
import com.querydsl.core.types.dsl.BooleanExpression;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Slice;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import static com.nameless.spin_off.entity.collection.QCollectedPost.collectedPost;
import static com.nameless.spin_off.entity.comment.QCommentInPost.commentInPost;
import static com.nameless.spin_off.entity.enums.post.PostPublicEnum.DEFAULT_POST_PUBLIC;
import static com.nameless.spin_off.entity.enums.post.PostPublicEnum.FOLLOW_POST_PUBLIC;
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

    public Optional<Post> findOneByIdWithHashtagAndMovieAndMember(Long postId) {
         return Optional.ofNullable(getQueryFactory()
                 .selectFrom(post)
                 .join(post.postedHashtags, postedHashtag).fetchJoin()
                 .join(post.member, member).fetchJoin()
                 .join(post.movie, movie).fetchJoin()
                 .where(post.id.eq(postId))
                 .fetchFirst());
    }

    public PublicOfPostStatus findPublicByPostId(Long postId) {
        return getQueryFactory()
                .select(post.publicOfPostStatus)
                .from(post)
                .where(post.id.eq(postId))
                .fetchFirst();
    }

    public Optional<IdAndPublicPostDto> findPublicPostByCommentId(Long commentId) {
        Tuple tuple = getQueryFactory()
                .select(post.id, post.publicOfPostStatus)
                .from(commentInPost)
                .join(commentInPost.post, post)
                .where(commentInPost.id.eq(commentId))
                .fetchFirst();

        if (tuple == null) {
            return Optional.empty();
        } else {
            return Optional.of(new PostDto.IdAndPublicPostDto(
                    tuple.get(post.id), tuple.get(post.publicOfPostStatus)));
        }
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

    public Long countComment(Long memberId, Long postId, List<Long> blockedMemberIds) {
        return getQueryFactory()
                .select(commentInPost.id)
                .from(commentInPost)
                .where(
                        commentInPost.post.id.eq(postId),
                        commentInPost.member.id.notIn(blockedMemberIds))
                .fetchCount();
    }

    public Boolean isFollowMembersPost(Long memberId, Long postId) {
        Integer fetchOne = getQueryFactory()
                .selectOne()
                .from(post)
                .join(post.member, member)
                .leftJoin(member.followingMembers, followedMember)
                .where(
                        post.id.eq(postId).and(
                                followedMember.followingMember.id.eq(memberId)))
                .fetchFirst();

        return fetchOne != null;
    }

    public Long findOwnerIdByPostId(Long postId) {
        return getQueryFactory()
                .select(post.member.id)
                .from(post)
                .where(
                        post.id.eq(postId))
                .fetchFirst();
    }

    public Boolean isExist(Long id) {
        Integer fetchOne = getQueryFactory()
                .selectOne()
                .from(post)
                .where(post.id.eq(id))
                .fetchFirst();

        return fetchOne != null;
    }

    public Boolean isExistCollectedPost(List<Long> collectionIds, Long postId) {
        Integer fetchOne = getQueryFactory()
                .selectOne()
                .from(collectedPost)
                .where(
                        collectedPost.collection.id.in(collectionIds),
                        collectedPost.post.id.eq(postId))
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
                        post.title.contains(keyword),
                        memberNotIn(blockedMemberIds)));
    }

    public Slice<MainPagePostDto> findAllSlicedForMainPage(Pageable pageable, Long memberId, List<Long> blockedMemberIds) {
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
                .where(post.publicOfPostStatus.in(DEFAULT_POST_PUBLIC.getPrivacyBound()),
                        memberNotIn(blockedMemberIds),
                        memberNotEq(memberId)));
    }

    public Slice<MainPagePostDto> findAllByFollowingMemberSlicedForMainPage(
            Pageable pageable, Long memberId, List<Long> blockedMemberIds) {

        return applySlicing(pageable, contentQuery -> contentQuery
                .select(new QPostDto_MainPagePostDto(
                        post.id, post.title, member.id, member.nickname, member.profileImg, post.thumbnailUrl))
                .from(post)
                .join(post.member, member)
                .join(member.followingMembers, followedMember)
                .where(
                        followedMember.followingMember.id.eq(memberId),
                        memberNotIn(blockedMemberIds),
                        post.publicOfPostStatus.in(FOLLOW_POST_PUBLIC.getPrivacyBound())));
    }

    public Slice<MainPagePostDto> findAllByFollowedHashtagsSlicedForMainPage(
            Pageable pageable, List<Long> followedMovies, List<Long> followedMemberIds,
            List<Long> blockedMemberIds, Long memberId) {

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
                        memberNotIn(followedMemberIds),
                        memberNotIn(blockedMemberIds),
                        post.publicOfPostStatus.in(DEFAULT_POST_PUBLIC.getPrivacyBound())));
    }

    public Slice<MainPagePostDto> findAllByFollowedMoviesSlicedForMainPage(
            Pageable pageable, List<Long> followedMemberIds, List<Long> blockedMemberIds, Long memberId) {
        return applySlicing(pageable, contentQuery -> contentQuery
                .select(new QPostDto_MainPagePostDto(
                        post.id, post.title, member.id, member.nickname, member.profileImg, post.thumbnailUrl))
                .from(post)
                .join(post.member, member)
                .join(post.movie, movie)
                .join(movie.followingMembers, followedMovie)
                .where(
                        followedMovie.member.id.eq(memberId),
                        memberNotIn(followedMemberIds),
                        memberNotIn(blockedMemberIds),
                        post.publicOfPostStatus.in(DEFAULT_POST_PUBLIC.getPrivacyBound())));
    }

    public Slice<SearchPageAtHashtagPostDto> findAllByHashtagsSlicedForSearchPage(
            Pageable pageable, List<Hashtag> hashtags, List<Long> blockedMemberIds) {
        return applySlicing(pageable, contentQuery -> contentQuery
                .selectDistinct(new QPostDto_SearchPageAtHashtagPostDto(
                        post.id, post.title, member.id, member.nickname, member.profileImg, post.thumbnailUrl))
                .from(post)
                .join(post.member, member)
                .join(post.postedHashtags, postedHashtag)
                .where(postedHashtag.hashtag.in(hashtags),
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

    private BooleanExpression memberNotEq(Long memberId) {
        return memberId != null ? member.id.ne(memberId) : null;
    }
    private BooleanExpression memberIn(List<Long> memberIds) {
        return memberIds.isEmpty() ? null : member.id.in(memberIds);
    }

    private BooleanExpression memberNotIn(List<Long> members) {
        return members.isEmpty() ? null : member.id.notIn(members);
    }
    private BooleanExpression postedMovieIn(List<Movie> movies) {
        return movies.isEmpty() ? null : post.movie.in(movies);
    }
    private BooleanExpression postedMovieNotIn(List<Long> movies) {
        return movies.isEmpty() ? null : post.movie.id.notIn(movies).or(post.movie.isNull());
    }
    private BooleanExpression postedHashtagIn(List<Hashtag> hashtags) {
        return hashtags.isEmpty() ? null : postedHashtag.hashtag.in(hashtags);
    }
    private BooleanExpression postedHashtagNotIn(List<Hashtag> hashtags) {
        return hashtags.isEmpty() ? null : postedHashtag.hashtag.notIn(hashtags).or(post.postedHashtags.isEmpty());
    }
}
