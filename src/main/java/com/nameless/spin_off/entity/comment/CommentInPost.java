package com.nameless.spin_off.entity.comment;

import com.nameless.spin_off.entity.listener.BaseTimeEntity;
import com.nameless.spin_off.entity.member.Member;
import com.nameless.spin_off.entity.post.Post;
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
public class CommentInPost extends BaseTimeEntity {

    @Id
    @GeneratedValue
    @Column(name="comment_in_post_id")
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "post_id")
    @NotNull
    private Post post;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "member_id")
    @NotNull
    private Member member;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "parent_id")
    private CommentInPost parent;

    @OneToMany(mappedBy = "parent", fetch = FetchType.LAZY)
    private List<CommentInPost> children = new ArrayList<>();

    private Boolean isDeleted;
    private String content;

    @OneToMany(mappedBy = "commentInPost", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private List<LikedCommentInPost> likedCommentInPosts = new ArrayList<>();

    //==연관관계 메소드==//

    public void addChildren(CommentInPost commentInPost) {
        this.children.add(commentInPost);
        commentInPost.updateParent(this);
    }

    public void addCommentLike(LikedCommentInPost likedCommentInPost) {
        this.likedCommentInPosts.add(likedCommentInPost);
        likedCommentInPost.updateCommentInPost(this);
    }

    //==생성 메소드==//
    public static CommentInPost createCommentInPost(Member member, String content, CommentInPost parent) {

        CommentInPost commentInPost = new CommentInPost();
        commentInPost.updateMember(member);
        commentInPost.updateContent(content);
        commentInPost.updateIsDeletedToFalse();

        if ( parent != null) {
            parent.addChildren(commentInPost);
        }

        return commentInPost;

    }

    //==수정 메소드==//
    public void updatePost(Post post) {
        this.post = post;
    }

    private void updateContent(String content) {
        this.content = content;
    }

    private void updateMember(Member member) {
        this.member = member;
    }

    private void updateParent(CommentInPost parent) {
        this.parent = parent;
    }

    private void updateIsDeletedToFalse() {
        isDeleted = Boolean.FALSE;
    }

    private void updateIsDeletedToTrue() {
        isDeleted = Boolean.TRUE;
    }

    //==비즈니스 로직==//

    //==조회 로직==//

}
