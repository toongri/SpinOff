package com.nameless.spin_off.repository.comment;

import com.nameless.spin_off.entity.comment.CommentInPost;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface CommentInPostRepository extends JpaRepository<CommentInPost, Long> {

    @Query("SELECT DISTINCT parent FROM CommentInPost parent " +
            "LEFT JOIN FETCH parent.children children " +
            "LEFT JOIN FETCH parent.member m " +
            "LEFT JOIN FETCH children.member mchildren " +
            "WHERE parent.parent IS NULL AND parent.post.id = :postId " +
            "ORDER BY parent.id DESC, children.id DESC")
    List<CommentInPost> findParentsByPostWithChildrenOrderByIdDESC(@Param("postId") Long postId);
}
