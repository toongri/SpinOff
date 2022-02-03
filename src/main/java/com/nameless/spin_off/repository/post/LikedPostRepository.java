package com.nameless.spin_off.repository.post;

import com.nameless.spin_off.entity.post.LikedPost;
import org.springframework.data.jpa.repository.JpaRepository;

public interface LikedPostRepository extends JpaRepository<LikedPost, Long> {
}
