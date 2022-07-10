package com.nameless.spin_off.repository.post;

import com.nameless.spin_off.entity.post.Post;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

public interface PostRepository extends JpaRepository<Post, Long> {

    @Query("SELECT DISTINCT post FROM Post post " +
            "LEFT JOIN FETCH post.collectedPosts collectedPost " +
            "WHERE post.id = :id")
    Optional<Post> findOneByIdWithCollectedPost(@Param("id") Long id);

    @Query("SELECT DISTINCT post FROM Post post " +
            "LEFT JOIN FETCH post.postedMedias postedMedia " +
            "WHERE post.id = :id")
    Optional<Post> findOneByIdWithPostedMedia(@Param("id") Long id);

    @Query("SELECT DISTINCT p FROM Post p " +
            "JOIN FETCH p.viewedPostByIps view " +
            "WHERE (view.createdDate > :time)")
    List<Post> findAllByViewAfterTime(@Param("time") LocalDateTime time);
}
