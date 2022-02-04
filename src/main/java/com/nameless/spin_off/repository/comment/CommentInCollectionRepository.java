package com.nameless.spin_off.repository.comment;

import com.nameless.spin_off.entity.comment.CommentInCollection;
import com.nameless.spin_off.entity.comment.CommentInPost;
import org.springframework.data.jpa.repository.JpaRepository;

public interface CommentInCollectionRepository extends JpaRepository<CommentInCollection, Long> {
}
