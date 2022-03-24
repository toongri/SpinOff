package com.nameless.spin_off.repository.post;

import com.nameless.spin_off.entity.post.Post;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.Optional;

public interface PostRepository extends JpaRepository<Post, Long> {

    @Query("SELECT DISTINCT post FROM Post post " +
            "LEFT JOIN FETCH post.collectedPosts collectedPost " +
            "WHERE post.id = :id")
    Optional<Post> findOneByIdWithCollectedPost(@Param("id") Long id);
}
