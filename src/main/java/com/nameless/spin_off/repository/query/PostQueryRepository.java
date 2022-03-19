package com.nameless.spin_off.repository.query;

import com.nameless.spin_off.dto.PostDto.MainPagePostDto;
import com.nameless.spin_off.dto.PostDto.SearchPageAtAllPostDto;
import com.nameless.spin_off.dto.PostDto.SearchPageAtHashtagPostDto;
import com.nameless.spin_off.dto.QPostDto_MainPagePostDto;
import com.nameless.spin_off.dto.QPostDto_SearchPageAtAllPostDto;
import com.nameless.spin_off.dto.QPostDto_SearchPageAtHashtagPostDto;
import com.nameless.spin_off.entity.hashtag.Hashtag;
import com.nameless.spin_off.entity.member.Member;
import com.nameless.spin_off.entity.movie.Movie;
import com.nameless.spin_off.entity.post.Post;
import com.nameless.spin_off.repository.support.Querydsl4RepositorySupport;
import com.querydsl.core.types.dsl.BooleanExpression;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Slice;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;

import static com.nameless.spin_off.entity.collection.QCollectedPost.collectedPost;
import static com.nameless.spin_off.entity.enums.post.PostPublicEnum.DEFAULT_POST_PUBLIC;
import static com.nameless.spin_off.entity.enums.post.PostPublicEnum.FOLLOW_POST_PUBLIC;
import static com.nameless.spin_off.entity.hashtag.QPostedHashtag.postedHashtag;
import static com.nameless.spin_off.entity.member.QMember.member;
import static com.nameless.spin_off.entity.movie.QMovie.movie;
import static com.nameless.spin_off.entity.post.QLikedPost.likedPost;
import static com.nameless.spin_off.entity.post.QPost.post;
import static com.nameless.spin_off.entity.post.QViewedPostByIp.viewedPostByIp;

@Repository
public class PostQueryRepository extends Querydsl4RepositorySupport {

    public PostQueryRepository() {
        super(Post.class);
    }

    public Long getPostOwnerId(Long postId) {
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
            String keyword, Pageable pageable, List<Member> blockedMembers) {
        return applySlicing(pageable, contentQuery -> contentQuery
                .select(new QPostDto_SearchPageAtAllPostDto(
                        post.id, post.title, member.id, member.nickname, member.profileImg, post.thumbnailUrl))
                .from(post)
                .join(post.member, member)
                .where(
                        post.title.contains(keyword),
                        memberNotIn(blockedMembers)));
    }

    public Slice<MainPagePostDto> findAllSlicedForMainPage(Pageable pageable, Member user, List<Member> blockedMembers) {
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
                        memberNotIn(blockedMembers),
                        memberNotEq(user)));
    }

    public Slice<MainPagePostDto> findAllByFollowingMemberSlicedForMainPage(
            Pageable pageable, List<Member> followedMembers, List<Member> blockedMembers) {

        return applySlicing(pageable, contentQuery -> contentQuery
                .select(new QPostDto_MainPagePostDto(
                        post.id, post.title, member.id, member.nickname, member.profileImg, post.thumbnailUrl))
                .from(post)
                .join(post.member, member)
                .where(
                        memberIn(followedMembers),
                        memberNotIn(blockedMembers),
                        post.publicOfPostStatus.in(FOLLOW_POST_PUBLIC.getPrivacyBound())));
    }

    public Slice<MainPagePostDto> findAllByFollowedHashtagsSlicedForMainPage(
            Pageable pageable, List<Movie> followedMovies, List<Member> followedMembers,
            List<Hashtag> followedHashtags, List<Member> blockedMembers) {

        return applySlicing(pageable, contentQuery -> contentQuery
                .selectDistinct(new QPostDto_MainPagePostDto(
                        post.id, post.title, member.id, member.nickname, member.profileImg, post.thumbnailUrl))
                .from(post)
                .join(post.member, member)
                .join(post.postedHashtags, postedHashtag)
                .leftJoin(post.movie, movie)
                .where(
                        postedMovieNotIn(followedMovies),
                        memberNotIn(followedMembers),
                        postedHashtagIn(followedHashtags),
                        memberNotIn(blockedMembers),
                        post.publicOfPostStatus.in(DEFAULT_POST_PUBLIC.getPrivacyBound())));
    }

    public Slice<MainPagePostDto> findAllByFollowedMoviesSlicedForMainPage(
            Pageable pageable, List<Movie> followedMovies, List<Member> followedMembers,
            List<Member> blockedMembers) {
        return applySlicing(pageable, contentQuery -> contentQuery
                .select(new QPostDto_MainPagePostDto(
                        post.id, post.title, member.id, member.nickname, member.profileImg, post.thumbnailUrl))
                .from(post)
                .join(post.member, member)
                .where(
                        memberNotIn(followedMembers),
                        postedMovieIn(followedMovies),
                        memberNotIn(blockedMembers),
                        post.publicOfPostStatus.in(DEFAULT_POST_PUBLIC.getPrivacyBound())));
    }

    public Slice<SearchPageAtHashtagPostDto> findAllByHashtagsSlicedForSearchPage(
            Pageable pageable, List<Hashtag> hashtags, List<Member> blockedMembers) {
        return applySlicing(pageable, contentQuery -> contentQuery
                .selectDistinct(new QPostDto_SearchPageAtHashtagPostDto(
                        post.id, post.title, member.id, member.nickname, member.profileImg, post.thumbnailUrl))
                .from(post)
                .join(post.member, member)
                .join(post.postedHashtags, postedHashtag)
                .where(postedHashtag.hashtag.in(hashtags),
                        memberNotIn(blockedMembers),
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

    private BooleanExpression memberNotEq(Member user) {
        return user != null ? member.ne(user) : null;
    }
    private BooleanExpression memberIn(List<Member> members) {
        return members.isEmpty() ? null : member.in(members);
    }
    private BooleanExpression memberNotIn(List<Member> members) {
        return members.isEmpty() ? null : member.notIn(members);
    }
    private BooleanExpression postedMovieIn(List<Movie> movies) {
        return movies.isEmpty() ? null : post.movie.in(movies);
    }
    private BooleanExpression postedMovieNotIn(List<Movie> movies) {
        return movies.isEmpty() ? null : post.movie.notIn(movies).or(post.movie.isNull());
    }
    private BooleanExpression postedHashtagIn(List<Hashtag> hashtags) {
        return hashtags.isEmpty() ? null : postedHashtag.hashtag.in(hashtags);
    }
    private BooleanExpression postedHashtagNotIn(List<Hashtag> hashtags) {
        return hashtags.isEmpty() ? null : postedHashtag.hashtag.notIn(hashtags).or(post.postedHashtags.isEmpty());
    }
}
