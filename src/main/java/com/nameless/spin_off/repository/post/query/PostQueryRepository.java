package com.nameless.spin_off.repository.post.query;

import com.nameless.spin_off.dto.PostDto;
import com.nameless.spin_off.entity.post.Post;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Slice;

import java.time.LocalDateTime;
import java.util.List;

public interface PostQueryRepository {
    Slice<PostDto.MainPagePost> findPostsOrderByCreatedDateBySlicing(Pageable pageable);
    Slice<PostDto.MainPagePost> findPostsOrderByPopularityBySlicingAfterLocalDateTime(
            Pageable pageable, LocalDateTime startDateTime, LocalDateTime endDateTime);
    Post testFindOne(Long id);
    public List<Post> testFindAll();
}
