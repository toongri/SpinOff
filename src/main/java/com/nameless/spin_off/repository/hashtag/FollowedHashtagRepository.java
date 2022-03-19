package com.nameless.spin_off.repository.hashtag;

import com.nameless.spin_off.entity.hashtag.FollowedHashtag;
import org.springframework.data.jpa.repository.JpaRepository;

public interface FollowedHashtagRepository extends JpaRepository<FollowedHashtag, Long> {
}
