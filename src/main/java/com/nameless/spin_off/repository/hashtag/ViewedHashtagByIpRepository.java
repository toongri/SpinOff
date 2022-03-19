package com.nameless.spin_off.repository.hashtag;

import com.nameless.spin_off.entity.hashtag.ViewedHashtagByIp;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ViewedHashtagByIpRepository extends JpaRepository<ViewedHashtagByIp, Long> {
}
