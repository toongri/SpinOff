package com.nameless.spin_off.repository.post;

import com.nameless.spin_off.entity.collections.Collection;
import com.nameless.spin_off.entity.post.Post;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.Optional;

public interface PostRepository extends JpaRepository<Post, Long> {

    @Query("SELECT DISTINCT post FROM Post post " +
            "LEFT JOIN FETCH post.likedPosts likedPost " +
            "WHERE post.id = :id")
    Optional<Post> findOneByIdWithLikedPost(@Param("id") Long id);

    @Query("SELECT DISTINCT post FROM Post post " +
            "LEFT JOIN FETCH post.viewedPostByIps viewdPostByIp " +
            "WHERE post.id = :id")
    Optional<Post> findOneByIdWithViewedByIp(@Param("id") Long id);

    @Query("SELECT DISTINCT post FROM Post post " +
            "LEFT JOIN FETCH post.commentInPosts comment " +
            "WHERE post.id = :id")
    Optional<Post> findOneByIdWithComment(@Param("id") Long id);

    @Query("SELECT DISTINCT post FROM Post post " +
            "LEFT JOIN FETCH post.collectedPosts collectedPost " +
            "WHERE post.id = :id")
    Optional<Post> findOneByIdWithCollectedPost(@Param("id") Long id);

    @Query("SELECT post FROM Post post " +
            "LEFT JOIN FETCH post.member m " +
            "LEFT JOIN FETCH m.complains complain " +
            "WHERE post.id = :id")
    Optional<Post> findOneByIdWithComplainOfMember(@Param("id") Long id);
}
