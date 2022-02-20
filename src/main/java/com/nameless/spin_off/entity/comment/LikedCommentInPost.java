package com.nameless.spin_off.entity.comment;

import com.nameless.spin_off.entity.listener.BaseTimeEntity;
import com.nameless.spin_off.entity.member.Member;
import com.sun.istack.NotNull;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

import javax.persistence.*;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class LikedCommentInPost extends BaseTimeEntity {

    @Id
    @GeneratedValue
    @Column(name="liked_comment_in_post_id")
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "member_id")
    @NotNull
    private Member member;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "comment_in_post_id")
    @NotNull
    private CommentInPost commentInPost;


    //==연관관계 메소드==//

    //==생성 메소드==//
    public static LikedCommentInPost createLikedCommentInPost(Member member, CommentInPost commentInPost) {
        LikedCommentInPost likedCommentInPost = new LikedCommentInPost();
        likedCommentInPost.updateMember(member);
        likedCommentInPost.updateCommentInPost(commentInPost);

        return likedCommentInPost;
    }

    //==수정 메소드==//
    public void updateCommentInPost(CommentInPost commentInPost) {
        this.commentInPost = commentInPost;
    }
    public void updateMember(Member member) {
        this.member = member;
    }
    //==비즈니스 로직==//

    //==조회 로직==//

}
