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
    Optional<Post> findOneByIdFetchJoinLikedPost(@Param("id") Long id);

    @Query("SELECT DISTINCT post FROM Post post " +
            "LEFT JOIN FETCH post.viewedPostByIps viewdPostByIp " +
            "WHERE post.id = :id " +
            "ORDER BY viewdPostByIp.id DESC")
    Optional<Post> findOneByIdFetchJoinViewedByIpOrderByViewedIpId(@Param("id") Long id);

    @Query("SELECT DISTINCT post FROM Post post " +
            "LEFT JOIN FETCH post.commentInPosts comment " +
            "WHERE post.id = :id")
    Optional<Post> findOneByIdIncludeComment(@Param("id") Long id);
}
