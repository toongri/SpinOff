package com.nameless.spin_off.repository.post;

import com.nameless.spin_off.entity.post.Hashtag;
import com.nameless.spin_off.entity.post.PostedHashtag;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface HashtagRepository extends JpaRepository<Hashtag, Long> {
    List<Hashtag> findAllByContentIn(List<String> contents);
}
