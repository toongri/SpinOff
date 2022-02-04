package com.nameless.spin_off.repository.comment;

import com.nameless.spin_off.entity.comment.CommentInPost;
import com.nameless.spin_off.entity.post.Post;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface CommentInPostRepository extends JpaRepository<CommentInPost, Long> {

    @Query("SELECT parent FROM CommentInPost parent JOIN FETCH parent.children " +
            "WHERE parent.parent IS NULL AND parent.post = :post ORDER BY parent.createdDate DESC")
    List<CommentInPost> findParentByPostIncludeChildrenOrderByDesc(@Param("post") Post post);
}
