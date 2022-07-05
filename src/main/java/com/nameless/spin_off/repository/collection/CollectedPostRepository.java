package com.nameless.spin_off.repository.collection;

import com.nameless.spin_off.entity.collection.CollectedPost;
import com.nameless.spin_off.entity.collection.Collection;
import com.nameless.spin_off.entity.post.Post;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface CollectedPostRepository extends JpaRepository<CollectedPost, Long> {
    List<CollectedPost> findAllByPost(Post post);
    List<CollectedPost> findAllByCollection(Collection collection);
}
