package com.nameless.spin_off.repository.post.query;

import com.nameless.spin_off.dto.PostDto;
import com.nameless.spin_off.dto.PostDto.MainPagePost;
import com.nameless.spin_off.dto.QPostDto_MainPagePost;
import com.nameless.spin_off.entity.member.QMember;
import com.nameless.spin_off.entity.post.*;
import com.querydsl.jpa.impl.JPAQuery;
import com.querydsl.jpa.impl.JPAQueryFactory;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Slice;
import org.springframework.data.domain.SliceImpl;
import org.springframework.data.support.PageableExecutionUtils;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;

import static com.nameless.spin_off.entity.member.QMember.member;
import static com.nameless.spin_off.entity.post.QPost.post;

@Repository
@RequiredArgsConstructor
public class PostQueryRepository {

    private final JPAQueryFactory jpaQueryFactory;

    public Slice<MainPagePost> findPostsOrderByCreatedDateBySlicing(
            Pageable pageable) {

        List<MainPagePost> content = jpaQueryFactory
                .select(new QPostDto_MainPagePost(
                        member.nickname,
                        post.title,
                        member.profileImg))
                .from(post)
                .join(member).fetchJoin()
                .offset(pageable.getOffset())
                .limit(pageable.getPageSize() + 1)
                .orderBy(post.createdDate.desc())
                .fetch();

        boolean hasNext = false;
        if (content.size() > pageable.getPageSize()) {
            content.remove(pageable.getPageSize());
            hasNext = true;
        }

        return new SliceImpl<>(content, pageable, hasNext);

//        JPAQuery<Post> countQuery = jpaQueryFactory
//                .selectFrom(post)
//                .where(post.createdDate.after(localDateTime));

//        return PageableExecutionUtils.getPage(posts, pageable, () -> countQuery.fetch().size());
    }

    public Slice<MainPagePost> findPostsOrderByPopularityBySlicingAfterLocalDateTime(
            Pageable pageable, LocalDateTime startDateTime, LocalDateTime endDateTime) {

        List<MainPagePost> content = jpaQueryFactory
                .select(new QPostDto_MainPagePost(
                        member.nickname,
                        post.title,
                        member.profileImg))
                .from(post)
                .join(member).fetchJoin()
                .where(post.createdDate.between(startDateTime, endDateTime))
                .offset(pageable.getOffset())
                .limit(pageable.getPageSize() + 1)
                .orderBy(post.collectionCount.add(post.commentCount.add(post.likeCount).add(post.viewCount)).desc())
                .fetch();

        boolean hasNext = false;
        if (content.size() > pageable.getPageSize()) {
            content.remove(pageable.getPageSize());
            hasNext = true;
        }

        return new SliceImpl<>(content, pageable, hasNext);

    }

    public Post testFindOne(Long id) {
        return jpaQueryFactory
                .select(post)
                .from(post)
                .leftJoin(post.member, member).fetchJoin()
                .where(post.id.eq(id))
                .fetchOne();
    }

    public List<Post> testFindAll() {
        return jpaQueryFactory
                .select(post)
                .from(post)
                .leftJoin(post.member, member).fetchJoin()
                .fetch();
    }
}
