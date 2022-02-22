package com.nameless.spin_off.repository.post;

import com.nameless.spin_off.entity.post.ViewedPostByIp;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ViewedPostByIpRepository extends JpaRepository<ViewedPostByIp, Long> {
}
