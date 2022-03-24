package com.nameless.spin_off.repository.comment;

import com.nameless.spin_off.entity.collection.Collection;
import com.nameless.spin_off.entity.comment.CommentInCollection;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface CommentInCollectionRepository extends JpaRepository<CommentInCollection, Long> {

    @Query("SELECT DISTINCT parent FROM CommentInCollection parent " +
            "LEFT JOIN FETCH parent.children children " +
            "WHERE parent.parent IS NULL AND parent.collection = :collection")
    List<CommentInCollection> findParentsByCollectionIdWithChildren(
            @Param("collection") Collection collection);
}
