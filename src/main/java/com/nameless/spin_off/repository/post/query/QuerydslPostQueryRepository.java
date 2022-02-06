package com.nameless.spin_off.repository.post.query;

import com.nameless.spin_off.dto.PostDto.MainPagePostDto;
import com.nameless.spin_off.dto.QPostDto_MainPagePostDto;
import com.nameless.spin_off.entity.post.*;
import com.querydsl.jpa.impl.JPAQueryFactory;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Slice;
import org.springframework.data.domain.SliceImpl;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;

import static com.nameless.spin_off.entity.member.QMember.member;
import static com.nameless.spin_off.entity.post.QPost.post;

@Repository
@RequiredArgsConstructor
public class QuerydslPostQueryRepository implements PostQueryRepository {

    private final JPAQueryFactory jpaQueryFactory;

    @Override
    public Slice<MainPagePostDto> findPostsOrderByCreatedDateBySlicing(Pageable pageable) {

        List<MainPagePostDto> content = jpaQueryFactory
                .select(new QPostDto_MainPagePostDto(
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

    @Override
    public Slice<MainPagePostDto> findPostsOrderByPopularityBySlicingAfterLocalDateTime(
            Pageable pageable, LocalDateTime startDateTime, LocalDateTime endDateTime) {

        List<MainPagePostDto> content = jpaQueryFactory
                .select(new QPostDto_MainPagePostDto(
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
