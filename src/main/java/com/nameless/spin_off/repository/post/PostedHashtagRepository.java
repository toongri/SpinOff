package com.nameless.spin_off.repository.post;

import com.nameless.spin_off.entity.post.Hashtag;
import com.nameless.spin_off.entity.post.PostedHashtag;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface PostedHashtagRepository extends JpaRepository<PostedHashtag, Long> {

}
