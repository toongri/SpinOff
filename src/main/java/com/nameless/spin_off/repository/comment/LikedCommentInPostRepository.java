package com.nameless.spin_off.repository.comment;

import com.nameless.spin_off.entity.comment.LikedCommentInPost;
import org.springframework.data.jpa.repository.JpaRepository;

public interface LikedCommentInPostRepository extends JpaRepository<LikedCommentInPost, Long> {

}
