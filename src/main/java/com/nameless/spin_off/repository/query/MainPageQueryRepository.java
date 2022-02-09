package com.nameless.spin_off.repository.query;

import com.nameless.spin_off.dto.PostDto;
import com.nameless.spin_off.entity.post.Post;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Slice;

import java.time.LocalDateTime;

public interface MainPageQueryRepository {
    Slice<Post> findPostsOrderByIdBySlicing(Pageable pageable);
    Slice<Post> findPostsOrderByPopularityBySlicingAfterLocalDateTime(
            Pageable pageable, LocalDateTime startDateTime, LocalDateTime endDateTime);
}
