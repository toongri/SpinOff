package com.nameless.spin_off.repository.comment;

import com.nameless.spin_off.entity.comment.CommentInPost;
import org.springframework.data.jpa.repository.JpaRepository;

public interface CommentInPostRepository extends JpaRepository<CommentInPost, Long> {
}
