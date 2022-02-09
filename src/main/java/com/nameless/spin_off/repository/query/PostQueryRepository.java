package com.nameless.spin_off.repository.query;

import com.nameless.spin_off.dto.PostDto;
import com.nameless.spin_off.entity.post.Post;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Slice;

import java.time.LocalDateTime;
import java.util.List;

public interface PostQueryRepository {
    Post testFindOne(Long id);
    public List<Post> testFindAll();
}
