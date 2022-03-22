package com.nameless.spin_off.repository.comment;

import com.nameless.spin_off.entity.comment.CommentInPost;
import com.nameless.spin_off.entity.post.Post;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface CommentInPostRepository extends JpaRepository<CommentInPost, Long> {

    @Query("SELECT DISTINCT parent FROM CommentInPost parent " +
            "LEFT JOIN FETCH parent.children children " +
            "LEFT JOIN FETCH parent.member m " +
            "LEFT JOIN FETCH children.member mchildren " +
            "WHERE parent.parent IS NULL AND parent.post = :post " +
            "ORDER BY parent.id DESC, children.id DESC")
    List<CommentInPost> findParentsByPostWithChildrenOrderByIdDESC(@Param("post") Post post);

    @Query("SELECT DISTINCT parent FROM CommentInPost parent " +
            "WHERE parent.parent IS NULL AND parent.post = :post")
    List<CommentInPost> findParentsByPost(@Param("post") Post post);

    @Query("SELECT DISTINCT comment FROM CommentInPost comment " +
            "LEFT JOIN FETCH comment.likedCommentInPosts likedComment " +
            "WHERE comment.id = :id")
    Optional<CommentInPost> findOneByIdWithLikedComment(@Param("id") Long id);

    @Query("SELECT DISTINCT commentInPost FROM CommentInPost commentInPost " +
            "JOIN FETCH commentInPost.member m " +
            "LEFT JOIN FETCH m.complains complain " +
            "WHERE commentInPost.id = :id")
    Optional<CommentInPost> findOneByIdWithComplainOfMember(@Param("id") Long id);
}
