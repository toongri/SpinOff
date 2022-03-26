package com.nameless.spin_off.entity.comment;

import com.nameless.spin_off.entity.collection.Collection;
import com.nameless.spin_off.entity.enums.ErrorEnum;
import com.nameless.spin_off.entity.listener.BaseTimeEntity;
import com.nameless.spin_off.entity.member.Member;
import com.nameless.spin_off.exception.comment.AlreadyLikedCommentInCollectionException;
import com.sun.istack.NotNull;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

import javax.persistence.*;
import java.util.ArrayList;
import java.util.List;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class CommentInCollection extends BaseTimeEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name="comment_in_collection_id")
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "collection_id")
    @NotNull
    private Collection collection;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "member_id")
    @NotNull
    private Member member;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "parent_id")
    private CommentInCollection parent;

    @OneToMany(mappedBy = "parent", fetch = FetchType.LAZY)
    private List<CommentInCollection> children = new ArrayList<>();

    private Boolean isDeleted;
    private String content;

    @OneToMany(mappedBy = "commentInCollection", cascade = CascadeType.ALL, fetch = FetchType.LAZY, orphanRemoval = true)
    private List<LikedCommentInCollection> likedCommentInCollections = new ArrayList<>();

    //==연관관계 메소드==//

    public void addChildren(CommentInCollection commentInCollection) {
        this.children.add(commentInCollection);
        commentInCollection.updateParent(this);
    }

    public Long addCommentLike(Member member) {
        LikedCommentInCollection likedCommentInCollection =
                LikedCommentInCollection.createLikedCommentInCollection(member, this);

        this.likedCommentInCollections.add(likedCommentInCollection);

        return likedCommentInCollection.getId();
    }

    //==생성 메소드==//
    public static CommentInCollection createCommentInCollection(
            Member member, String content, CommentInCollection parent, Collection collection) {

        CommentInCollection commentInCollection = new CommentInCollection();
        commentInCollection.updateMember(member);
        commentInCollection.updateContent(content);
        commentInCollection.updateIsDeletedToFalse();
        collection.addCommentInCollection(commentInCollection);

        if ( parent != null) {
            parent.addChildren(commentInCollection);
        }

        return commentInCollection;
    }

    public static CommentInCollection createCommentInCollection(Long id) {

        CommentInCollection commentInCollection = new CommentInCollection();
        commentInCollection.updateId(id);

        return commentInCollection;
    }

    //==수정 메소드==//
    public void updateId(Long id) {
        this.id = id;
    }
    public void updateCollection(Collection collection) {
        this.collection = collection;
    }

    private void updateContent(String content) {
        this.content = content;
    }

    private void updateMember(Member member) {
        this.member = member;
    }

    private void updateParent(CommentInCollection parent) {
        this.parent = parent;
    }

    private void updateIsDeletedToFalse() {
        isDeleted = Boolean.FALSE;
    }

    private void updateIsDeletedToTrue() {
        isDeleted = Boolean.TRUE;
    }

    //==비즈니스 로직==//
    public Long insertLikedComment(Member member) throws AlreadyLikedCommentInCollectionException {
        if (isNotAlreadyMemberLikeComment(member)) {
            return addCommentLike(member);
        } else {
            throw new AlreadyLikedCommentInCollectionException(ErrorEnum.ALREADY_LIKED_COMMENT_IN_COLLECTION);
        }
    }
    //==조회 로직==//
    public Boolean isNotAlreadyMemberLikeComment(Member member) {
        return this.likedCommentInCollections.stream()
                .noneMatch(likedCommentInCollection -> likedCommentInCollection.getMember().equals(member));
    }
}
