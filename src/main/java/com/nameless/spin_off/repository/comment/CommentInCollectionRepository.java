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

    @Query("SELECT DISTINCT parent FROM CommentInCollection parent LEFT JOIN FETCH parent.children children " +
            "WHERE parent.parent IS NULL AND parent.collection = :collection ORDER BY parent.id DESC, children.id DESC")
    List<CommentInCollection> findParentsByCollectionIdIncludeChildrenOrderByParentIdAndChildIdDesc(
            @Param("collection") Collection collection);

    @Query("SELECT DISTINCT parent FROM CommentInCollection parent " +
            "WHERE parent.parent IS NULL AND parent.collection = :collection")
    List<CommentInCollection> findParentsByCollection(@Param("collection") Collection collection);
}
