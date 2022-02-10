package com.nameless.spin_off.repository.post;

import com.nameless.spin_off.entity.member.Member;
import com.nameless.spin_off.entity.post.Hashtag;
import com.nameless.spin_off.entity.post.PostedHashtag;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface HashtagRepository extends JpaRepository<Hashtag, Long> {
    List<Hashtag> findAllByContentIn(List<String> contents);

}
