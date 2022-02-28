package com.nameless.spin_off.repository.query;

import com.nameless.spin_off.dto.PostDto.MainPagePostDto;
import com.nameless.spin_off.dto.PostDto.SearchPageAtAllPostDto;
import com.nameless.spin_off.dto.QPostDto_MainPagePostDto;
import com.nameless.spin_off.dto.QPostDto_SearchPageAtAllPostDto;
import com.nameless.spin_off.entity.hashtag.Hashtag;
import com.nameless.spin_off.entity.member.Member;
import com.nameless.spin_off.entity.movie.Movie;
import com.nameless.spin_off.entity.post.Post;
import com.nameless.spin_off.repository.support.Querydsl4RepositorySupport;
import com.querydsl.core.types.dsl.BooleanExpression;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Slice;
import org.springframework.stereotype.Repository;

import java.util.List;

import static com.nameless.spin_off.entity.enums.post.PostPublicEnum.DEFAULT_POST_PUBLIC;
import static com.nameless.spin_off.entity.enums.post.PostPublicEnum.FOLLOW_POST_PUBLIC;
import static com.nameless.spin_off.entity.hashtag.QPostedHashtag.postedHashtag;
import static com.nameless.spin_off.entity.member.QMember.member;
import static com.nameless.spin_off.entity.post.QPost.post;

@Repository
public class PostQueryRepository extends Querydsl4RepositorySupport {

    public PostQueryRepository() {
        super(Post.class);
    }

    public Slice<SearchPageAtAllPostDto> findAllSlicedSearchPageAtAll(String keyword, Pageable pageable) {
        return applySlicing(pageable, contentQuery -> contentQuery
                .select(new QPostDto_SearchPageAtAllPostDto(
                        post.id, post.title, member.id, member.nickname, member.profileImg, post.thumbnailUrl))
                .from(post)
                .join(post.member, member)
                .where(post.title.contains(keyword)));
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
                .where(memberIn(followedMembers),
                        memberNotIn(blockedMembers),
                        post.publicOfPostStatus.in(FOLLOW_POST_PUBLIC.getPrivacyBound())));
    }

    public Slice<MainPagePostDto> findAllByFollowedHashtagsSlicedForMainPage(
            Pageable pageable, List<Hashtag> followedHashtags, List<Member> blockedMembers) {

        return applySlicing(pageable, contentQuery -> contentQuery
                .selectDistinct(new QPostDto_MainPagePostDto(
                        post.id, post.title, member.id, member.nickname, member.profileImg, post.thumbnailUrl))
                .from(post)
                .join(post.member, member)
                .join(post.postedHashtags, postedHashtag)
                .where(postedHashtagIn(followedHashtags),
                        memberNotIn(blockedMembers),
                        post.publicOfPostStatus.in(DEFAULT_POST_PUBLIC.getPrivacyBound())));
    }

    public Slice<MainPagePostDto> findAllByFollowedMoviesSlicedForMainPage(
            Pageable pageable, List<Movie> followedMovies, List<Member> blockedMembers) {
        return applySlicing(pageable, contentQuery -> contentQuery
                .select(new QPostDto_MainPagePostDto(
                        post.id, post.title, member.id, member.nickname, member.profileImg, post.thumbnailUrl))
                .from(post)
                .join(post.member, member)
                .where(postedMovieIn(followedMovies),
                        memberNotIn(blockedMembers),
                        post.publicOfPostStatus.in(DEFAULT_POST_PUBLIC.getPrivacyBound())));
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
    private BooleanExpression postedHashtagIn(List<Hashtag> hashtags) {
        return hashtags.isEmpty() ? null : postedHashtag.hashtag.in(hashtags);
    }
}
