package com.nameless.spin_off.repository.collections;

import com.nameless.spin_off.entity.collections.Collection;
import com.nameless.spin_off.entity.collections.ViewedCollectionByIp;
import com.nameless.spin_off.entity.post.Post;
import com.nameless.spin_off.entity.post.ViewedPostByIp;
import org.springframework.data.jpa.repository.JpaRepository;

import java.time.LocalDateTime;
import java.util.Optional;

public interface ViewedCollectionByIpRepository extends JpaRepository<ViewedCollectionByIp, Long> {
}
