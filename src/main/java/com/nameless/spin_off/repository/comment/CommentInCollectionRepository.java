package com.nameless.spin_off.repository.comment;

import com.nameless.spin_off.entity.collections.Collection;
import com.nameless.spin_off.entity.comment.CommentInCollection;
import com.nameless.spin_off.entity.comment.CommentInPost;
import com.nameless.spin_off.entity.post.Post;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface CommentInCollectionRepository extends JpaRepository<CommentInCollection, Long> {

    @Query("SELECT DISTINCT parent FROM CommentInCollection parent  LEFT JOIN FETCH parent.children " +
            "WHERE parent.parent IS NULL AND parent.collection = :collection ORDER BY parent.createdDate DESC")
    List<CommentInCollection> findParentsByCollectionIncludeChildrenOrderByDesc(
            @Param("collection") Collection collection);
}
