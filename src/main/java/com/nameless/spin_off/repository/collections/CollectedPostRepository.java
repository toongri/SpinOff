package com.nameless.spin_off.repository.collections;

import com.nameless.spin_off.entity.collections.CollectedPost;
import com.nameless.spin_off.entity.collections.Collection;
import com.nameless.spin_off.entity.post.Post;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface CollectedPostRepository extends JpaRepository<CollectedPost, Long> {
    List<CollectedPost> findAllByPost(Post post);
}
