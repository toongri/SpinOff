package com.nameless.spin_off.repository.comment;

import com.nameless.spin_off.entity.collections.Collection;
import com.nameless.spin_off.entity.comment.CommentInCollection;
import com.nameless.spin_off.entity.comment.CommentInPost;
import com.nameless.spin_off.entity.post.Post;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface CommentInCollectionRepository extends JpaRepository<CommentInCollection, Long> {

    @Query("SELECT DISTINCT parent FROM CommentInCollection parent LEFT JOIN FETCH parent.children children " +
            "WHERE parent.parent IS NULL AND parent.collection = :collection")
    List<CommentInCollection> findParentsByCollectionIdWithChildren(
            @Param("collection") Collection collection);

    @Query("SELECT DISTINCT parent FROM CommentInCollection parent " +
            "WHERE parent.parent IS NULL AND parent.collection = :collection")
    List<CommentInCollection> findParentsByCollection(@Param("collection") Collection collection);

    @Query("SELECT DISTINCT comment FROM CommentInCollection comment " +
            "LEFT JOIN FETCH comment.likedCommentInCollections likedComment " +
            "LEFT JOIN FETCH likedComment.member m " +
            "WHERE comment.id = :id")
    Optional<CommentInCollection> findOneByIdWithLikedComment(@Param("id") Long id);
}
