package com.nameless.spin_off.repository.hashtag;

import com.nameless.spin_off.entity.hashtag.Hashtag;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface HashtagRepository extends JpaRepository<Hashtag, Long> {
    List<Hashtag> findAllByContentIn(List<String> contents);

}
