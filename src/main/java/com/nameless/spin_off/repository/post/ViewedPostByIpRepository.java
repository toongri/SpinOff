package com.nameless.spin_off.repository.post;

import com.nameless.spin_off.entity.collections.Collection;
import com.nameless.spin_off.entity.post.Post;
import com.nameless.spin_off.entity.post.ViewedPostByIp;
import org.springframework.data.jpa.repository.JpaRepository;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

public interface ViewedPostByIpRepository extends JpaRepository<ViewedPostByIp, Long> {

    Optional<ViewedPostByIp> findOneByCreatedDateBetweenAndIpAndPost(
            LocalDateTime startDate, LocalDateTime endDate, String ip, Post post);
}
