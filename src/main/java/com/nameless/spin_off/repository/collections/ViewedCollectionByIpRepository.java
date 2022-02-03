package com.nameless.spin_off.repository.collections;

import com.nameless.spin_off.entity.collections.ViewedCollectionByIp;
import com.nameless.spin_off.entity.post.ViewedPostByIp;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ViewedCollectionByIpRepository extends JpaRepository<ViewedCollectionByIp, Long> {
}
