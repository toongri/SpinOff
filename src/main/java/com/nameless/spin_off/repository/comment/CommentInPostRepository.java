package com.nameless.spin_off.repository.comment;

import com.nameless.spin_off.entity.comment.CommentInPost;
import com.nameless.spin_off.entity.post.Post;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface CommentInPostRepository extends JpaRepository<CommentInPost, Long> {

    @Query("SELECT DISTINCT parent FROM CommentInPost parent LEFT JOIN FETCH parent.children children " +
            "WHERE parent.parent IS NULL AND parent.post = :post ORDER BY parent.id DESC, children.id DESC")
    List<CommentInPost> findParentsByPostIncludeChildrenOrderByParentIdAndChildIdDesc(@Param("post") Post post);

    @Query("SELECT DISTINCT parent FROM CommentInPost parent " +
            "WHERE parent.parent IS NULL AND parent.post = :post")
    List<CommentInPost> findParentsByPost(@Param("post") Post post);
}