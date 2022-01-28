package com.nameless.spin_off.repository.post;

import com.nameless.spin_off.entity.post.PostedMedia;
import org.springframework.data.jpa.repository.JpaRepository;

public interface MediaRepository extends JpaRepository<PostedMedia, Long> {
}
